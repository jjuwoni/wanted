package com.ohgiraffers.jwtrestapi.product.dto;

import lombok.Data;

@Data
public class ProductAndCategoryDTO {

    private int productCode;
    private String productName;
    private String productPrice;
    private String productDescription;
    private String productOrderable;
    private CategoryDTO category;
    private String productImageUrl;
    private Long productStock;

}
