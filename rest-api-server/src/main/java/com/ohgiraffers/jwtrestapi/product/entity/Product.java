package com.ohgiraffers.jwtrestapi.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter // Setter 추가 (수업을 위해서 권장하지는 않지만 추가)
@ToString
@Table(name = "tbl_product")
@Builder(toBuilder = true)
public class Product {

    @Id
    @Column(name = "product_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productCode;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private String productPrice;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "product_orderable")
    private String productOrderable;

    @Column(name = "category_code")
    private int categoryCode;

    @Column(name = "product_image_url")
    private String productImageUrl;

    @Column(name = "product_stock")
    private Long productStock;
}
