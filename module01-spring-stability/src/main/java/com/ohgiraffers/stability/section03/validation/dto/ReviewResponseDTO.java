package com.ohgiraffers.stability.section03.validation.dto;

import com.ohgiraffers.stability.section03.validation.entity.Review;
import java.time.LocalDateTime;

/**
 * 리뷰 정보 응답을 위한 DTO 클래스
 * 
 * <p>클라이언트에게 리뷰 정보를 전달할 때 사용하는 데이터 전송 객체다.
 * 엔티티의 민감한 정보는 제외하고 필요한 정보만 포함한다.</p>
 */
public class ReviewResponseDTO {

    private Long reviewId;
    private Long memberId;
    private String memberName;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private Double rating;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer helpfulCount;
    private Boolean verifiedPurchase;
    private Review.ReviewStatus status;

    public ReviewResponseDTO() {}

    public ReviewResponseDTO(Long reviewId, Long memberId, String memberName, Long bookId, 
                           String bookTitle, String bookAuthor, Double rating, String title, 
                           String content, LocalDateTime createdAt, LocalDateTime updatedAt, 
                           Integer helpfulCount, Boolean verifiedPurchase, Review.ReviewStatus status) {
        this.reviewId = reviewId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.helpfulCount = helpfulCount;
        this.verifiedPurchase = verifiedPurchase;
        this.status = status;
    }

    // Getters and Setters
    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public Review.ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(Review.ReviewStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReviewResponseDTO{" +
                "reviewId=" + reviewId +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", helpfulCount=" + helpfulCount +
                ", verifiedPurchase=" + verifiedPurchase +
                ", status=" + status +
                '}';
    }
} 