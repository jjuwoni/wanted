package com.ohgiraffers.jwtrestapi.product.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private int productCode;
    private String productName;
    private String productPrice;
    private String productDescription;
    private String productOrderable;
    private int categoryCode;
    private String productImageUrl;
    private Long productStock;

}
