package com.ohgiraffers.jwtrestapi.review.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ReviewDTO {

    private int reviewCode;
    private int productCode;
    private int memberCode;
    private String reviewTitle;
    private String reviewContent;
    private String reviewCreateDate;

}
