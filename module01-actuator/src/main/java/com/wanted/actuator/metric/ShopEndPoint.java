package com.wanted.actuator.metric;

import com.wanted.actuator.order.OrderRepository;
import com.wanted.actuator.product.ProductRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

/* comment.
*   해당 클래스는 Actuator 에서 기본적으로 제공하는
*   health, info, metrics 뿐만 아니라 쇼핑 서비스의
*   현재 상태를 운영자가 한 번에 확인할 수 있는 커스텀
*   Actuator 엔드포인트를 정의하는 클래스이다.
* */

@Component
@Endpoint(id="shop")
public class ShopEndPoint {

    // 해당 클래스는 /actuator/shop GET 방식으로 조회 가능

    // 관리자가 전체 상품 수, 주문 수 등을 확인할 수 있는
    // 정보를 포함
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public ShopEndPoint(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    /* comment.
    *   @ReadOperation 은 Actuator 엔드포인트의 읽기 작업을 의미한다.
    *   HTTP GET /actuator/shop 요청을 보내면 해당 객체가 연결되어 응답을 한다.
    * */
    @ReadOperation
    public ShopSummary summary() {
        return new ShopSummary(
                "STEP 1",
                productRepository.count(),
                orderRepository.count()
        );

    }

    public record ShopSummary(
            String step,
            long productCount,
            long orderCount
    ) {}
}
