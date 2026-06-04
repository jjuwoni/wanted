package com.wanted.actuator.order;

import com.wanted.actuator.metric.ShopMetrics;
import com.wanted.actuator.order.dto.CreateOrderRequest;
import com.wanted.actuator.order.dto.OrderResponse;
import com.wanted.actuator.payment.PaymentService;
import com.wanted.actuator.product.Product;
import com.wanted.actuator.product.ProductRepository;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    /* comment.
    *   우리가 만든 Custom Metric 을 비즈니스 코드에 끼워넣기 위한
    *   준비과정
    * */
    private final ShopMetrics shopMetrics;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            PaymentService paymentService,
            ShopMetrics shopMetrics
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.paymentService = paymentService;
        this.shopMetrics = shopMetrics;
    }

    /* comment.
    *   우리가 만든 커스텀 Metric (주문 생성 시간 확인)
    *   비즈니스 코드에 넣기
    * */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        // 타이머 동작 시작
        Timer.Sample sample = shopMetrics.startTimer();

        try{
            Order order = new Order();
            long orderAmount = 0;

            for (CreateOrderRequest.Item item : request.items()) {
                Product product = findProduct(item.productId());
                product.decreaseStock(item.quantity());
                order.addItem(new OrderItem(product, item.quantity()));
                orderAmount += product.getPrice() * item.quantity();
            }

            paymentService.pay();

            Order savedOrder = orderRepository.save(order);
            // 주문 성공 시 기록 할 Metric
            shopMetrics.recordCreatedOrder(savedOrder.getItems().size(), orderAmount);
            log.info("주문이 생성되었습니다. orderId={}", savedOrder.getId());
            return OrderResponse.from(savedOrder);
        } catch(RuntimeException exception) {
            // 주문 실패 시 생성할 Metric
            shopMetrics.recordFailedOrder(exception);
            throw exception;
        } finally {
            // 위에서 진행 된 타이머를 종료
            shopMetrics.stopOrderCreationTimer(sample);
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
        return OrderResponse.from(order);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }
}
