package com.ohgiraffers.jwtrestapi.review.entity;

import com.ohgiraffers.jwtrestapi.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder(toBuilder = true)
@Table(name = "tbl_review")
public class ReviewAndMember {

    @Id
    @Column(name = "review_code")
    private int reviewCode;

    @Column(name = "product_code")
    private int productCode;

    @ManyToOne
    @JoinColumn(name = "member_code")
    private Member member;

    @Column(name = "review_title")
    private String reviewTitle;

    @Column(name = "review_content")
    private String reviewContent;

    @Column(name = "review_create_date")
    private String reviewCreateDate;

}
