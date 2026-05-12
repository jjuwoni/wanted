package com.ohgiraffers.jwtrestapi.purchase.dto;

import lombok.Data;

@Data
public class PurchaseDTO {
    // 구매 시 사용할 DTO 필드 구성
    private String memberId;    // test01
    private String orderAddress;
    private int orderAmount;
    private String orderEmail;
    private String orderPhone;
    private String orderReceiver;
    private int productCode;

}
