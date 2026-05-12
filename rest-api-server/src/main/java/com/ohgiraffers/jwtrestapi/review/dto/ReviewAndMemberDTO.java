package com.ohgiraffers.jwtrestapi.review.dto;

import com.ohgiraffers.jwtrestapi.member.dto.MemberDTO;
import lombok.Data;

@Data
public class ReviewAndMemberDTO {

    private int reviewCode;
    private int productCode;
    private MemberDTO member;
    private String reviewTitle;
    private String reviewContent;
    private String reviewCreateDate;


}
