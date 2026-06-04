package com.wanted.actuator.product.dto;



import com.wanted.actuator.product.Product;

import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        long price,
        int stock,
        LocalDateTime createdAt
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt()
        );
    }
}
