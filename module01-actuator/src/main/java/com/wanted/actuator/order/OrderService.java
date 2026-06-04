package com.wanted.actuator.order;

import com.wanted.actuator.order.dto.CreateOrderRequest;
import com.wanted.actuator.order.dto.OrderResponse;
import com.wanted.actuator.payment.PaymentService;
import com.wanted.actuator.product.Product;
import com.wanted.actuator.product.ProductRepository;
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

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            PaymentService paymentService
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();

        for (CreateOrderRequest.Item item : request.items()) {
            Product product = findProduct(item.productId());
            product.decreaseStock(item.quantity());
            order.addItem(new OrderItem(product, item.quantity()));
        }

        paymentService.pay();

        Order savedOrder = orderRepository.save(order);
        log.info("주문이 생성되었습니다. orderId={}", savedOrder.getId());
        return OrderResponse.from(savedOrder);
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
