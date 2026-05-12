package com.ohgiraffers.jwtrestapi.purchase.dto;

import com.ohgiraffers.jwtrestapi.product.dto.ProductDTO;
import lombok.Data;

@Data
public class OrderAndProductDTO {

    private int orderCode;
    // 제품 관련 정보 객체
    private ProductDTO product;
    private int orderMember;
    private String orderPhone;
    private String orderEmail;
    private String orderReceiver;
    private String orderAddress;
    private String orderAmount;
    private String orderDate;
}
