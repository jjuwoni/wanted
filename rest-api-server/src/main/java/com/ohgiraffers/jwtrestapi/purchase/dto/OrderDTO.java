package com.ohgiraffers.jwtrestapi.purchase.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class OrderDTO {

    private int orderCode;
    private int productCode;
    private int orderMember;
    private String orderPhone;
    private String orderEmail;
    private String orderReceiver;
    private String orderAddress;
    private String orderAmount;
    private String orderDate;
}
