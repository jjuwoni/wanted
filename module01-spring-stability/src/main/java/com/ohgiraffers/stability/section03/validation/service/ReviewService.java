package com.ohgiraffers.stability.section03.validation.service;

import com.ohgiraffers.stability.section01.exception.entity.Book;
import com.ohgiraffers.stability.section01.exception.exception.BookNotFoundException;
import com.ohgiraffers.stability.section01.exception.repository.BookRepository;
import com.ohgiraffers.stability.section02.logging.entity.Member;
import com.ohgiraffers.stability.section02.logging.repository.MemberRepository;
import com.ohgiraffers.stability.section03.validation.dto.ReviewCreateDTO;
import com.ohgiraffers.stability.section03.validation.dto.ReviewResponseDTO;
import com.ohgiraffers.stability.section03.validation.dto.ReviewUpdateDTO;
import com.ohgiraffers.stability.section03.validation.entity.Review;
import com.ohgiraffers.stability.section03.validation.repository.ReviewRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 도서 리뷰 관리 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * <p>리뷰의 생성, 수정, 삭제 및 조회 기능을 제공한다.
 * Bean Validation을 활용하여 입력 데이터의 유효성을 검증한다.</p>
 */
@Service
@Transactional(readOnly = true)
@Validated
public class ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;


    @Autowired
    public ReviewService(ReviewRepository reviewRepository, MemberRepository memberRepository, BookRepository bookRepository) {
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;

    }

    /* 목차. 1. 리뷰 생성 및 수정 메서드 */
    
    /**
     * 새로운 리뷰를 생성한다.
     * 
     * @param reviewCreateDTO 리뷰 생성 정보
     * @return 생성된 리뷰 정보
     * @throws IllegalArgumentException 회원이나 도서가 존재하지 않거나 이미 리뷰가 존재하는 경우 발생
     */
    @Transactional
    public ReviewResponseDTO createReview(@Valid ReviewCreateDTO reviewCreateDTO) {
        log.info("[s3][ReviewService] 리뷰 생성 요청 - 회원 ID: {}, 도서 ID: {}, 평점: {}", 
                reviewCreateDTO.getMemberId(), reviewCreateDTO.getBookId(), reviewCreateDTO.getRating());
        
        // 회원 조회 및 검증
        Member member = memberRepository.findById(reviewCreateDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + reviewCreateDTO.getMemberId()));
        
        if (!member.getActive()) {
            throw new IllegalArgumentException("비활성화된 회원은 리뷰를 작성할 수 없습니다: " + reviewCreateDTO.getMemberId());
        }
        
        // 도서 조회 및 검증
        Book book = bookRepository.findById(reviewCreateDTO.getBookId())
                .orElseThrow(() -> new BookNotFoundException(reviewCreateDTO.getBookId()));
        
        // 중복 리뷰 검증
        if (reviewRepository.existsByMemberAndBook(member, book)) {
            throw new IllegalArgumentException("이미 해당 도서에 대한 리뷰를 작성하셨습니다.");
        }
        
        // 리뷰 생성
        Review review = new Review(
                member, 
                book, 
                reviewCreateDTO.getRating(), 
                reviewCreateDTO.getTitle(), 
                reviewCreateDTO.getContent(), 
                reviewCreateDTO.getVerifiedPurchase()
        );
        
        Review savedReview = reviewRepository.save(review);

        
        log.info("[s3][ReviewService] 리뷰 생성 완료 - 리뷰 ID: {}, 회원: {}, 도서: {}, 평점: {}점", 
                savedReview.getReviewId(), member.getMemberName(), book.getTitle(), savedReview.getRating());
        
        return convertToResponseDTO(savedReview);
    }

    /**
     * 기존 리뷰를 수정한다.
     * 
     * @param reviewId 리뷰 ID
     * @param reviewUpdateDTO 리뷰 수정 정보
     * @return 수정된 리뷰 정보
     * @throws IllegalArgumentException 리뷰가 존재하지 않는 경우 발생
     */
    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, @Valid ReviewUpdateDTO reviewUpdateDTO) {
        log.info("[s3][ReviewService] 리뷰 수정 요청 - 리뷰 ID: {}, 새 평점: {}", reviewId, reviewUpdateDTO.getRating());
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다: " + reviewId));
        
        if (review.getStatus() != Review.ReviewStatus.ACTIVE) {
            throw new IllegalArgumentException("활성 상태의 리뷰만 수정할 수 있습니다: " + reviewId);
        }
        
        // 리뷰 수정
        review.updateReview(
                reviewUpdateDTO.getRating(), 
                reviewUpdateDTO.getTitle(), 
                reviewUpdateDTO.getContent()
        );
        
        Review savedReview = reviewRepository.save(review);
        
        log.info("[s3][ReviewService] 리뷰 수정 완료 - 리뷰 ID: {}, 새 평점: {}점", reviewId, savedReview.getRating());
        
        return convertToResponseDTO(savedReview);
    }

    /* 목차. 2. 리뷰 조회 메서드 */
    
    /**
     * 특정 도서의 리뷰 목록을 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 해당 도서의 리뷰 목록
     */
    public List<ReviewResponseDTO> findReviewsByBook(Long bookId) {
        log.debug("[s3][ReviewService] 도서별 리뷰 목록 조회 - 도서 ID: {}", bookId);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        List<Review> reviews = reviewRepository.findByBookAndStatus(book, Review.ReviewStatus.ACTIVE);
        
        log.debug("[s3][ReviewService] 도서별 리뷰 목록 조회 완료 - 도서: {}, 리뷰 수: {}개", book.getTitle(), reviews.size());
        
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원의 리뷰 목록을 조회한다.
     * 
     * @param memberId 회원 ID
     * @return 해당 회원의 리뷰 목록
     */
    public List<ReviewResponseDTO> findReviewsByMember(Long memberId) {
        log.debug("[s3][ReviewService] 회원별 리뷰 목록 조회 - 회원 ID: {}", memberId);
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));
        
        List<Review> reviews = reviewRepository.findByMemberAndStatus(member, Review.ReviewStatus.ACTIVE);
        
        log.debug("[s3][ReviewService] 회원별 리뷰 목록 조회 완료 - 회원: {}, 리뷰 수: {}개", member.getMemberName(), reviews.size());
        
        return reviews.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 도서의 평균 평점과 리뷰 통계를 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 평점 통계 정보
     */
    public Map<String, Object> getBookRatingStatistics(Long bookId) {
        log.debug("[s3][ReviewService] 도서 평점 통계 조회 - 도서 ID: {}", bookId);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        Double averageRating = reviewRepository.findAverageRatingByBookId(bookId);
        long totalReviews = reviewRepository.countActiveReviewsByBookId(bookId);
        List<Object[]> ratingDistribution = reviewRepository.findRatingDistributionByBookId(bookId);
        
        Map<String, Object> statistics = Map.of(
                "bookId", bookId,
                "bookTitle", book.getTitle(),
                "averageRating", averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0.0,
                "totalReviews", totalReviews,
                "ratingDistribution", ratingDistribution
        );
        
        log.info("[s3][ReviewService] 도서 평점 통계 - 도서: {}, 평균 평점: {}, 총 리뷰: {}개", 
                book.getTitle(), statistics.get("averageRating"), totalReviews);
        
        return statistics;
    }

    /* 목차. 3. 리뷰 관리 메서드 */
    
    /**
     * 리뷰에 도움이 되었다고 표시한다.
     * 
     * @param reviewId 리뷰 ID
     * @return 업데이트된 리뷰 정보
     */
    @Transactional
    public ReviewResponseDTO markAsHelpful(Long reviewId) {
        log.info("[s3][ReviewService] 리뷰 도움 표시 - 리뷰 ID: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다: " + reviewId));
        
        if (review.getStatus() != Review.ReviewStatus.ACTIVE) {
            throw new IllegalArgumentException("활성 상태의 리뷰만 도움 표시할 수 있습니다: " + reviewId);
        }
        
        review.incrementHelpfulCount();
        Review savedReview = reviewRepository.save(review);
        
        log.info("[s3][ReviewService] 리뷰 도움 표시 완료 - 리뷰 ID: {}, 도움 수: {}개", reviewId, savedReview.getHelpfulCount());
        
        return convertToResponseDTO(savedReview);
    }

    /**
     * 리뷰를 삭제한다.
     * 
     * @param reviewId 리뷰 ID
     * @param memberId 요청한 회원 ID (작성자 확인용)
     */
    @Transactional
    public void deleteReview(Long reviewId, Long memberId) {
        log.info("[s3][ReviewService] 리뷰 삭제 요청 - 리뷰 ID: {}, 요청 회원 ID: {}", reviewId, memberId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다: " + reviewId));
        
        // 작성자 확인
        if (!review.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }
        
        review.markAsDeleted();
        reviewRepository.save(review);
        
        log.info("[s3][ReviewService] 리뷰 삭제 완료 - 리뷰 ID: {}", reviewId);
    }

    /* 목차. 4. 유틸리티 메서드 */
    
    /**
     * Review 엔티티를 ReviewResponseDTO로 변환한다.
     * 
     * @param review 변환할 Review 엔티티
     * @return 변환된 ReviewResponseDTO
     */
    private ReviewResponseDTO convertToResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getReviewId(),
                review.getMember().getMemberId(),
                review.getMember().getMemberName(),
                review.getBook().getBookId(),
                review.getBook().getTitle(),
                review.getBook().getAuthor(),
                review.getRating(),
                review.getTitle(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getHelpfulCount(),
                review.getVerifiedPurchase(),
                review.getStatus()
        );
    }
} 