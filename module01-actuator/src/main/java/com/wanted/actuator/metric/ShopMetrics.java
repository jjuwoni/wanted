package com.wanted.actuator.metric;

import com.wanted.actuator.product.InsufficientStockException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class ShopMetrics {

    // MeterRegistry는 Micrometer의 핵심 진입점이다.
    // Counter, Timer, DistributionSummary 같은 Meter를 등록하고 기록하는 저장소 역할을 한다.
    // Spring Boot Actuator가 있으면 기본적으로 SimpleMeterRegistry가 만들어지고,
    // Prometheus 의존성을 추가하면 PrometheusMeterRegistry가 함께 사용된다.
    private final MeterRegistry meterRegistry;

    // Counter는 감소하지 않는 누적 값을 표현한다.
    // "주문이 성공한 횟수", "인기 상품 API가 호출된 횟수"처럼 사건 발생 수에 적합하다.
    private final Counter createdOrderCounter;
    private final Counter popularProductRequestCounter;

    // DistributionSummary는 시간(duration)이 아닌 값의 분포를 기록한다.
    // 주문당 상품 종류 수, 주문 금액처럼 "값이 얼마나 큰가"를 관찰할 때 사용한다.
    private final DistributionSummary orderItemSummary;
    private final DistributionSummary orderAmountSummary;

    // Timer는 실행 시간과 호출 수를 함께 기록한다.
    // Actuator에서 count, totalTime, max 같은 값을 확인할 수 있다.
    private final Timer orderCreationTimer;
    private final Timer paymentTimer;
    private final Timer popularProductQueryTimer;

    public ShopMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Counter: 애플리케이션 시작 이후 성공적으로 생성된 주문의 누적 개수다.
        // Counter는 감소하지 않는 누적값을 표현할 때 사용한다.
        this.createdOrderCounter = Counter.builder("shop.orders.created")
                .description("Number of successfully created orders")
                .register(meterRegistry);

        // Counter: 인기 상품 API가 호출된 누적 횟수다.
        // 쿼리 시간과 함께 보면 "느린 API가 얼마나 자주 호출되는가"를 설명할 수 있다.
        this.popularProductRequestCounter = Counter.builder("shop.products.popular.requests")
                .description("Number of popular product API requests")
                .register(meterRegistry);

        // DistributionSummary: 한 주문에 포함된 상품 종류 수의 분포를 기록한다.
        // 단순 합계뿐 아니라 count, total, max를 확인할 수 있다.
        this.orderItemSummary = DistributionSummary.builder("shop.orders.item.count")
                .description("Number of items included in a successfully created order")
                .baseUnit("items")
                .register(meterRegistry);

        // DistributionSummary: 성공한 주문의 주문 금액 분포를 기록한다.
        // 현업에서는 주문 건수보다 주문 금액이 비즈니스 상태를 더 잘 설명할 때가 많다.
        this.orderAmountSummary = DistributionSummary.builder("shop.orders.amount")
                .description("Order amount of successfully created orders")
                .baseUnit("won")
                .register(meterRegistry);

        // Timer: 주문 생성 전체 시간을 측정한다.
        // 느린 결제 서비스가 포함되므로 주문 지연을 관찰할 수 있다.
        this.orderCreationTimer = Timer.builder("shop.orders.creation.duration")
                .description("Time spent creating an order including payment")
                .register(meterRegistry);

        // Timer: 외부 결제 서비스 호출을 흉내 낸 500ms 대기를 별도로 측정한다.
        // 전체 주문 시간과 비교하면 외부 의존성이 차지하는 비율을 설명할 수 있다.
        this.paymentTimer = Timer.builder("shop.payment.duration")
                .description("Time spent waiting for the fake payment service")
                .register(meterRegistry);

        // Timer: 캐시 없이 매 요청 실행되는 인기 상품 집계 쿼리의 시간을 측정한다.
        this.popularProductQueryTimer = Timer.builder("shop.products.popular.query.duration")
                .description("Time spent executing the popular product aggregation query")
                .register(meterRegistry);
    }

    // Timer.Sample은 작업 시작 시점의 시간을 기억한다.
    // 실제 Timer는 작업이 끝난 뒤 sample.stop(timer)를 호출할 때 결정된다.
    // 이렇게 하면 try-finally로 감싸서 성공/실패 여부와 관계없이 시간을 기록할 수 있다.
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    // 주문 성공 메트릭은 단순한 메서드 실행 횟수가 아니라 "실제로 주문 저장에 성공했다"는
    // 비즈니스 의미를 가진다. 그래서 AOP로 숨기기보다 서비스에서 성공 지점에 명시적으로
    // 호출하는 것이 수업과 실무 모두에서 이해하기 쉽다.
    public void recordCreatedOrder(int itemCount, long orderAmount) {
        createdOrderCounter.increment();
        orderItemSummary.record(itemCount);
        orderAmountSummary.record(orderAmount);
    }

    // 주문 실패 메트릭은 주문 생성 흐름의 실패만 기록한다.
    // 전체 API 오류는 recordApiError에서 따로 기록한다.
    // 이 둘을 구분해야 "주문 도메인 문제"와 "HTTP 사용자 경험 문제"를 분리해서 볼 수 있다.
    public void recordFailedOrder(RuntimeException exception) {
        // 실패 원인은 제한된 태그 값만 사용한다. 예외 메시지를 태그로 넣으면
        // 메시지 종류만큼 시계열이 늘어날 수 있으므로 사용하지 않는다.
        Counter.builder("shop.orders.failed")
                .description("Number of failed order creation attempts")
                .tag("reason", failureReason(exception))
                .register(meterRegistry)
                .increment();
    }

    // ExceptionHandler에서 호출한다.
    // Validation 실패, 잘못된 요청, 서버 오류처럼 클라이언트에게 오류 응답이 반환된 횟수를
    // 집계한다. HTTP 상태 코드별 자동 메트릭(http.server.requests)도 있지만, 수업에서는
    // 제한된 reason 태그를 직접 설계해보는 예제로 사용한다.
    public void recordApiError(String reason) {
        // API 오류는 주문 실패와 별도로 기록한다. Validation 실패나 잘못된 조회처럼
        // 주문 생성 흐름이 아닌 오류도 HTTP 사용자 경험에는 영향을 주기 때문이다.
        Counter.builder("shop.api.errors")
                .description("Number of API errors returned to clients")
                .tag("reason", reason)
                .register(meterRegistry)
                .increment();
    }

    // 인기 상품 API 요청 수를 기록한다.
    // popularProductQueryTimer가 "얼마나 오래 걸렸는가"를 말해 준다면,
    // 이 Counter는 "얼마나 자주 호출되는가"를 말해 준다.
    // 운영 우선순위는 지연 시간과 호출 빈도를 함께 보고 판단해야 한다.
    public void recordPopularProductRequest() {
        popularProductRequestCounter.increment();
    }

    // 주문 생성 전체 시간 기록을 종료한다.
    // 현재는 결제 지연까지 포함하므로 shop.payment.duration과 비교해 병목 비중을 설명할 수 있다.
    public void stopOrderCreationTimer(Timer.Sample sample) {
        sample.stop(orderCreationTimer);
    }

    // 가짜 결제 서비스의 지연 시간을 기록한다.
    public void stopPaymentTimer(Timer.Sample sample) {
        sample.stop(paymentTimer);
    }

    // 인기 상품 집계 쿼리 시간을 기록한다.
    public void stopPopularProductQueryTimer(Timer.Sample sample) {
        sample.stop(popularProductQueryTimer);
    }

    // 태그 값은 반드시 제한된 집합으로 변환한다.
    // exception.getMessage()를 태그로 쓰면 메시지마다 새로운 시계열이 생길 수 있다.
    private String failureReason(RuntimeException exception) {
        if (exception instanceof InsufficientStockException) {
            return "insufficient_stock";
        }
        if (exception instanceof IllegalArgumentException) {
            return "invalid_request";
        }
        return "unexpected";
    }
}
