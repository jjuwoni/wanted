package com.ohgiraffers.stability.section03.validation.dto;

import jakarta.validation.constraints.*;
import com.ohgiraffers.stability.section03.validation.annotation.ValidRating;

/**
 * 리뷰 생성 요청을 위한 DTO 클래스
 * 
 * <p>
 * 클라이언트에서 새로운 리뷰를 생성할 때 사용하는 데이터 전송 객체다.
 * Bean Validation 어노테이션을 통해 입력 데이터의 유효성을 검증한다.
 * </p>
 */
public class ReviewCreateDTO {

    @NotNull(message = "회원 ID는 필수입니다")
    @Positive(message = "회원 ID는 양수여야 합니다")
    private Long memberId;

    @NotNull(message = "도서 ID는 필수입니다")
    @Positive(message = "도서 ID는 양수여야 합니다")
    private Long bookId;

    // @NotNull(message = "평점은 필수입니다")
    // @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
    // @Max(value = 5, message = "평점은 5점 이하여야 합니다")
    // private Integer rating;
    @ValidRating
    private Double rating;

    @NotBlank(message = "리뷰 제목은 필수입니다")
    @Size(min = 5, max = 100, message = "리뷰 제목은 5자 이상 100자 이하여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s\\.,!?\\-()]+$", message = "리뷰 제목에는 특수문자를 사용할 수 없습니다 (일부 문장부호 제외)")
    private String title;

    @NotBlank(message = "리뷰 내용은 필수입니다")
    @Size(min = 10, max = 2000, message = "리뷰 내용은 10자 이상 2000자 이하여야 합니다")
    private String content;

    @NotNull(message = "구매 확인 여부는 필수입니다")
    private Boolean verifiedPurchase;

    public ReviewCreateDTO() {
    }

    // public ReviewCreateDTO(Long memberId, Long bookId, Integer rating, String
    // title, String content, Boolean verifiedPurchase) {
    public ReviewCreateDTO(Long memberId, Long bookId, Double rating, String title, String content,
            Boolean verifiedPurchase) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.verifiedPurchase = verifiedPurchase;
    }

    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    // public Integer getRating() {
    // return rating;
    // }
    public Double getRating() {
        return rating;
    }

    // public void setRating(Integer rating) {
    // this.rating = rating;
    // }
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

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    @Override
    public String toString() {
        return "ReviewCreateDTO{" +
                "memberId=" + memberId +
                ", bookId=" + bookId +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", verifiedPurchase=" + verifiedPurchase +
                '}';
    }
}