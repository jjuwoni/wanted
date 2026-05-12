package com.ohgiraffers.jwtrestapi.purchase.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "tbl_order")
@Builder(toBuilder = true)
public class Order {

    @Id
    @Column(name = "order_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderCode;

    @Column(name = "product_code")
    private int productCode;

    @Column(name = "order_member")
    private int orderMember;

    @Column(name = "order_phone")
    private String orderPhone;

    @Column(name = "order_email")
    private String orderEmail;

    @Column(name = "order_receiver")
    private String orderReceiver;

    @Column(name = "order_address")
    private String orderAddress;

    @Column(name = "order_amount")
    private String orderAmount;

    @Column(name = "order_date")
    private String orderDate;

}
