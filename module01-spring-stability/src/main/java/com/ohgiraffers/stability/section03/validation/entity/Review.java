package com.ohgiraffers.stability.section03.validation.entity;

import com.ohgiraffers.stability.section01.exception.entity.Book;
import com.ohgiraffers.stability.section02.logging.entity.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.ohgiraffers.stability.section03.validation.annotation.ValidRating;

/**
 * 도서 리뷰 정보를 관리하는 엔티티 클래스
 * 
 * <p>도서관 시스템에서 회원이 작성한 도서 리뷰와 평점 정보를 저장하고 관리한다.
 * Bean Validation을 활용하여 데이터 무결성을 보장한다.</p>
 */
@Entity
@Table(name = "tbl_review", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "book_id"}))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull(message = "리뷰 작성자는 필수입니다")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "리뷰 대상 도서는 필수입니다")
    private Book book;

    @Column(name = "rating", nullable = false)
    @NotNull(message = "평점은 필수입니다")
    @ValidRating
    private Double rating;

    @Column(name = "title", nullable = false, length = 100)
    @NotBlank(message = "리뷰 제목은 필수입니다")
    @Size(min = 5, max = 100, message = "리뷰 제목은 5자 이상 100자 이하여야 합니다")
    private String title;

    @Column(name = "content", nullable = false, length = 2000)
    @NotBlank(message = "리뷰 내용은 필수입니다")
    @Size(min = 10, max = 2000, message = "리뷰 내용은 10자 이상 2000자 이하여야 합니다")
    private String content;

    @Column(name = "created_at", nullable = false)
    @PastOrPresent(message = "작성일은 현재 시간 이전이어야 합니다")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @PastOrPresent(message = "수정일은 현재 시간 이전이어야 합니다")
    private LocalDateTime updatedAt;

    @Column(name = "helpful_count", nullable = false)
    @Min(value = 0, message = "도움이 된 수는 0 이상이어야 합니다")
    private Integer helpfulCount;

    @Column(name = "verified_purchase", nullable = false)
    private Boolean verifiedPurchase;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "리뷰 상태는 필수입니다")
    private ReviewStatus status;

    protected Review() {}

    public Review(Member member, Book book, Double rating, String title, String content, Boolean verifiedPurchase) {
        this.member = member;
        this.book = book;
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.verifiedPurchase = verifiedPurchase;
        this.createdAt = LocalDateTime.now();
        this.helpfulCount = 0;
        this.status = ReviewStatus.ACTIVE;
    }

    // Getters
    public Long getReviewId() {
        return reviewId;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public Double getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    // Setters
    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    /**
     * 리뷰를 수정한다.
     * 
     * @param rating 새로운 평점
     * @param title 새로운 제목
     * @param content 새로운 내용
     */
    public void updateReview(Double rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 도움이 된 수를 증가시킨다.
     */
    public void incrementHelpfulCount() {
        this.helpfulCount++;
    }

    /**
     * 리뷰를 삭제 상태로 변경한다.
     */
    public void markAsDeleted() {
        this.status = ReviewStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 리뷰를 숨김 상태로 변경한다.
     */
    public void markAsHidden() {
        this.status = ReviewStatus.HIDDEN;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 리뷰 상태를 나타내는 열거형
     */
    public enum ReviewStatus {
        ACTIVE,    // 활성
        HIDDEN,    // 숨김
        DELETED    // 삭제
    }
} 