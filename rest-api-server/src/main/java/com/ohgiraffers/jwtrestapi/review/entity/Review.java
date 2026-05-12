package com.ohgiraffers.jwtrestapi.review.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Table(name = "tbl_review")
@Builder(toBuilder = true)
public class Review {

    @Id
    @Column(name = "review_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewCode;

    @Column(name = "product_code")
    private int productCode;

    @Column(name = "member_code")
    private int memberCode;

    @Column(name = "review_title")
    private String reviewTitle;

    @Column(name = "review_content")
    private String reviewContent;

    @Column(name = "review_create_date")
    private String reviewCreateDate;

}
