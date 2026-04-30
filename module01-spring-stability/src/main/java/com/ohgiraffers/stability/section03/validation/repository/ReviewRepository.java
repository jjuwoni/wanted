package com.ohgiraffers.stability.section03.validation.repository;

import com.ohgiraffers.stability.section01.exception.entity.Book;
import com.ohgiraffers.stability.section02.logging.entity.Member;
import com.ohgiraffers.stability.section03.validation.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 데이터 접근을 위한 Repository 인터페이스
 * 
 * <p>리뷰 엔티티에 대한 CRUD 작업과 커스텀 쿼리를 제공한다.
 * JpaRepository를 상속받아 기본적인 데이터베이스 작업을 지원한다.</p>
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 회원과 도서에 대한 리뷰를 조회한다.
     * 
     * @param member 회원 정보
     * @param book 도서 정보
     * @return 해당 회원이 해당 도서에 작성한 리뷰 (Optional)
     */
    Optional<Review> findByMemberAndBook(Member member, Book book);

    /**
     * 특정 회원과 도서에 대한 리뷰 존재 여부를 확인한다.
     * 
     * @param member 회원 정보
     * @param book 도서 정보
     * @return 리뷰 존재 여부
     */
    boolean existsByMemberAndBook(Member member, Book book);

    /**
     * 특정 도서의 활성 상태 리뷰 목록을 조회한다.
     * 
     * @param book 도서 정보
     * @return 해당 도서의 활성 리뷰 목록
     */
    List<Review> findByBookAndStatus(Book book, Review.ReviewStatus status);

    /**
     * 특정 회원의 활성 상태 리뷰 목록을 조회한다.
     * 
     * @param member 회원 정보
     * @param status 리뷰 상태
     * @return 해당 회원의 활성 리뷰 목록
     */
    List<Review> findByMemberAndStatus(Member member, Review.ReviewStatus status);

    /**
     * 특정 도서의 평균 평점을 계산한다.
     * 
     * @param bookId 도서 ID
     * @return 평균 평점
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.bookId = :bookId AND r.status = 'ACTIVE'")
    Double findAverageRatingByBookId(@Param("bookId") Long bookId);

    /**
     * 특정 도서의 활성 리뷰 수를 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 활성 리뷰 수
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.book.bookId = :bookId AND r.status = 'ACTIVE'")
    long countActiveReviewsByBookId(@Param("bookId") Long bookId);

    /**
     * 평점별 리뷰 수를 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 평점별 리뷰 수 (평점, 개수)
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.book.bookId = :bookId AND r.status = 'ACTIVE' GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> findRatingDistributionByBookId(@Param("bookId") Long bookId);

    /**
     * 도움이 많이 된 리뷰 목록을 조회한다.
     * 
     * @param bookId 도서 ID
     * @param limit 조회할 개수
     * @return 도움이 많이 된 리뷰 목록
     */
    @Query("SELECT r FROM Review r WHERE r.book.bookId = :bookId AND r.status = 'ACTIVE' ORDER BY r.helpfulCount DESC, r.createdAt DESC")
    List<Review> findMostHelpfulReviewsByBookId(@Param("bookId") Long bookId);

    /**
     * 최신 리뷰 목록을 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 최신 리뷰 목록
     */
    @Query("SELECT r FROM Review r WHERE r.book.bookId = :bookId AND r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    List<Review> findLatestReviewsByBookId(@Param("bookId") Long bookId);

    /**
     * 구매 확인된 리뷰 목록을 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 구매 확인된 리뷰 목록
     */
    @Query("SELECT r FROM Review r WHERE r.book.bookId = :bookId AND r.status = 'ACTIVE' AND r.verifiedPurchase = true ORDER BY r.createdAt DESC")
    List<Review> findVerifiedReviewsByBookId(@Param("bookId") Long bookId);
} 