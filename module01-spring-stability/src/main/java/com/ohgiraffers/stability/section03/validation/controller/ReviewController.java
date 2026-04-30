package com.ohgiraffers.stability.section03.validation.controller;

import com.ohgiraffers.stability.section03.validation.dto.ReviewCreateDTO;
import com.ohgiraffers.stability.section03.validation.dto.ReviewResponseDTO;
import com.ohgiraffers.stability.section03.validation.dto.ReviewUpdateDTO;
import com.ohgiraffers.stability.section03.validation.service.ReviewService;
import com.ohgiraffers.stability.section03.validation.annotation.ValidRating;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 도서 리뷰 관리 REST API 컨트롤러
 * 
 * <p>리뷰 관련 HTTP 요청을 처리하고 적절한 응답을 반환한다.
 * Bean Validation을 활용하여 입력 데이터의 유효성을 검증한다.</p>
 */
@RestController
@RequestMapping("/api/v1/reviews")
@Validated
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);
    
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /* 목차. 1. 리뷰 생성 및 수정 API */
    
    /**
     * 새로운 리뷰를 생성한다.
     * 
     * @param reviewCreateDTO 리뷰 생성 정보
     * @return 생성된 리뷰 정보
     */
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewCreateDTO reviewCreateDTO) {
        log.info("[s3][ReviewController] 리뷰 생성 요청 접수 - 회원 ID: {}, 도서 ID: {}", 
                reviewCreateDTO.getMemberId(), reviewCreateDTO.getBookId());
        
        ReviewResponseDTO review = reviewService.createReview(reviewCreateDTO);
        
        log.info("[s3][ReviewController] 리뷰 생성 요청 처리 완료 - 리뷰 ID: {}", review.getReviewId());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    /**
     * 기존 리뷰를 수정한다.
     * 
     * @param reviewId 리뷰 ID
     * @param reviewUpdateDTO 리뷰 수정 정보
     * @return 수정된 리뷰 정보
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable @Positive(message = "리뷰 ID는 양수여야 합니다") Long reviewId,
            @Valid @RequestBody ReviewUpdateDTO reviewUpdateDTO) {
        
        log.info("[s3][ReviewController] 리뷰 수정 요청 접수 - 리뷰 ID: {}", reviewId);
        
        ReviewResponseDTO review = reviewService.updateReview(reviewId, reviewUpdateDTO);
        
        log.info("[s3][ReviewController] 리뷰 수정 요청 처리 완료 - 리뷰 ID: {}", reviewId);
        return ResponseEntity.ok(review);
    }

    /* 목차. 2. 리뷰 조회 API */
    
    /**
     * 특정 도서의 리뷰 목록을 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 해당 도서의 리뷰 목록
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByBook(
            @PathVariable @Positive(message = "도서 ID는 양수여야 합니다") Long bookId) {
        
        log.info("[s3][ReviewController] 도서별 리뷰 목록 조회 요청 - 도서 ID: {}", bookId);
        
        List<ReviewResponseDTO> reviews = reviewService.findReviewsByBook(bookId);
        
        log.info("[s3][ReviewController] 도서별 리뷰 목록 조회 완료 - 도서 ID: {}, 리뷰 수: {}개", bookId, reviews.size());
        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 회원의 리뷰 목록을 조회한다.
     * 
     * @param memberId 회원 ID
     * @return 해당 회원의 리뷰 목록
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByMember(
            @PathVariable @Positive(message = "회원 ID는 양수여야 합니다") Long memberId) {
        
        log.info("[s3][ReviewController] 회원별 리뷰 목록 조회 요청 - 회원 ID: {}", memberId);
        
        List<ReviewResponseDTO> reviews = reviewService.findReviewsByMember(memberId);
        
        log.info("[s3][ReviewController] 회원별 리뷰 목록 조회 완료 - 회원 ID: {}, 리뷰 수: {}개", memberId, reviews.size());
        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 도서의 평점 통계를 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 평점 통계 정보
     */
    @GetMapping("/book/{bookId}/statistics")
    public ResponseEntity<Map<String, Object>> getBookRatingStatistics(
            @PathVariable @Positive(message = "도서 ID는 양수여야 합니다") Long bookId) {
        
        log.info("[s3][ReviewController] 도서 평점 통계 조회 요청 - 도서 ID: {}", bookId);
        
        Map<String, Object> statistics = reviewService.getBookRatingStatistics(bookId);
        
        log.info("[s3][ReviewController] 도서 평점 통계 조회 완료 - 도서 ID: {}", bookId);
        return ResponseEntity.ok(statistics);
    }

    /* 목차. 3. 리뷰 관리 API */
    
    /**
     * 리뷰에 도움이 되었다고 표시한다.
     * 
     * @param reviewId 리뷰 ID
     * @return 업데이트된 리뷰 정보
     */
    @PatchMapping("/{reviewId}/helpful")
    public ResponseEntity<ReviewResponseDTO> markAsHelpful(
            @PathVariable @Positive(message = "리뷰 ID는 양수여야 합니다") Long reviewId) {
        
        log.info("[s3][ReviewController] 리뷰 도움 표시 요청 - 리뷰 ID: {}", reviewId);
        
        ReviewResponseDTO review = reviewService.markAsHelpful(reviewId);
        
        log.info("[s3][ReviewController] 리뷰 도움 표시 완료 - 리뷰 ID: {}", reviewId);
        return ResponseEntity.ok(review);
    }

    /**
     * 리뷰를 삭제한다.
     * 
     * @param reviewId 리뷰 ID
     * @param memberId 요청한 회원 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, String>> deleteReview(
            @PathVariable @Positive(message = "리뷰 ID는 양수여야 합니다") Long reviewId,
            @RequestParam @Positive(message = "회원 ID는 양수여야 합니다") Long memberId) {
        
        log.info("[s3][ReviewController] 리뷰 삭제 요청 - 리뷰 ID: {}, 요청 회원 ID: {}", reviewId, memberId);
        
        reviewService.deleteReview(reviewId, memberId);
        
        Map<String, String> response = Map.of(
                "message", "리뷰가 성공적으로 삭제되었습니다.",
                "reviewId", reviewId.toString()
        );
        
        log.info("[s3][ReviewController] 리뷰 삭제 완료 - 리뷰 ID: {}", reviewId);
        return ResponseEntity.ok(response);
    }

    /* 목차. 4. 검증 테스트 API */
    
    /**
     * Bean Validation 테스트를 위한 API
     * 
     * @param testData 테스트 데이터
     * @return 검증 결과
     */
    @PostMapping("/test/validation")
    public ResponseEntity<Map<String, Object>> testValidation(@Valid @RequestBody ReviewCreateDTO testData) {
        log.info("[s3][ReviewController] Bean Validation 테스트 요청 - 데이터: {}", testData);
        
        Map<String, Object> response = Map.of(
                "message", "모든 검증을 통과했습니다.",
                "data", testData,
                "timestamp", java.time.LocalDateTime.now()
        );
        
        log.info("[s3][ReviewController] Bean Validation 테스트 완료 - 검증 성공");
        return ResponseEntity.ok(response);
    }

    /**
     * 경로 변수 검증 테스트를 위한 API
     * 
     * @param id 테스트할 ID
     * @return 검증 결과
     */
    @GetMapping("/test/path-validation/{id}")
    public ResponseEntity<Map<String, Object>> testPathValidation(
            @PathVariable @Positive(message = "ID는 양수여야 합니다") Long id) {
        
        log.info("[s3][ReviewController] 경로 변수 검증 테스트 요청 - ID: {}", id);
        
        Map<String, Object> response = Map.of(
                "message", "경로 변수 검증을 통과했습니다.",
                "id", id,
                "timestamp", java.time.LocalDateTime.now()
        );
        
        log.info("[s3][ReviewController] 경로 변수 검증 테스트 완료 - ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 요청 파라미터 검증 테스트를 위한 API
     * 
     * @param rating 테스트할 평점
     * @param title 테스트할 제목
     * @return 검증 결과
     */
    @GetMapping("/test/param-validation")
    public ResponseEntity<Map<String, Object>> testParamValidation(
            @RequestParam @ValidRating Double rating,
            @RequestParam @Size(min = 5, max = 100, message = "제목은 5자 이상 100자 이하여야 합니다") String title) {
        
        log.info("[s3][ReviewController] 요청 파라미터 검증 테스트 요청 - 평점: {}, 제목: {}", rating, title);
        
        Map<String, Object> response = Map.of(
                "message", "요청 파라미터 검증을 통과했습니다.",
                "rating", rating,
                "title", title,
                "timestamp", java.time.LocalDateTime.now()
        );
        
        log.info("[s3][ReviewController] 요청 파라미터 검증 테스트 완료");
        return ResponseEntity.ok(response);
    }
} 