package com.wanted.actuator.order.dto;


import com.wanted.actuator.order.Order;
import com.wanted.actuator.order.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        LocalDateTime createdAt,
        List<Item> items
) {

    public static OrderResponse from(Order order) {
        List<Item> items = order.getItems().stream()
                .map(Item::from)
                .toList();

        return new OrderResponse(order.getId(), order.getCreatedAt(), items);
    }

    public record Item(
            Long id,
            Long productId,
            String productName,
            long productPrice,
            int quantity
    ) {

        private static Item from(OrderItem orderItem) {
            return new Item(
                    orderItem.getId(),
                    orderItem.getProduct().getId(),
                    orderItem.getProduct().getName(),
                    orderItem.getProduct().getPrice(),
                    orderItem.getQuantity()
            );
        }
    }
}
