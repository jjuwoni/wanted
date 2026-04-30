package com.ohgiraffers.stability.section03.validation.dto;

import jakarta.validation.constraints.*;
import com.ohgiraffers.stability.section03.validation.annotation.ValidRating;

/**
 * 리뷰 수정 요청을 위한 DTO 클래스
 * 
 * <p>기존 리뷰의 평점, 제목, 내용을 수정할 때 사용하는 데이터 전송 객체다.
 * Bean Validation 어노테이션을 통해 수정 데이터의 유효성을 검증한다.</p>
 */
public class ReviewUpdateDTO {

    @NotNull(message = "평점은 필수입니다")
    @ValidRating
    private Double rating;

    @NotBlank(message = "리뷰 제목은 필수입니다")
    @Size(min = 5, max = 100, message = "리뷰 제목은 5자 이상 100자 이하여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s\\.,!?\\-()]+$", 
             message = "리뷰 제목에는 특수문자를 사용할 수 없습니다 (일부 문장부호 제외)")
    private String title;

    @NotBlank(message = "리뷰 내용은 필수입니다")
    @Size(min = 10, max = 2000, message = "리뷰 내용은 10자 이상 2000자 이하여야 합니다")
    private String content;

    public ReviewUpdateDTO() {}

    public ReviewUpdateDTO(Double rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ReviewUpdateDTO{" +
                "rating=" + rating +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
} 