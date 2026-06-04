package com.wanted.actuator.product.dto;

public record PopularProductResponse(
        Long id,
        String name,
        long price,
        long orderCount
) {
}
