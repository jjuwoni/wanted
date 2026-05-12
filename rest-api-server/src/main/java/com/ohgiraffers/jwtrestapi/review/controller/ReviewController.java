package com.ohgiraffers.jwtrestapi.review.controller;

import com.ohgiraffers.jwtrestapi.common.Criteria;
import com.ohgiraffers.jwtrestapi.common.PageDTO;
import com.ohgiraffers.jwtrestapi.common.PagingResponseDTO;
import com.ohgiraffers.jwtrestapi.common.ResponseDTO;
import com.ohgiraffers.jwtrestapi.review.dto.ReviewDTO;
import com.ohgiraffers.jwtrestapi.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ReviewController {

	private final ReviewService reviewService;
	
	@Autowired
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}
	
	@Operation(summary = "상품 리뷰 등록 요청", description = "해당 상품 리뷰 등록이 진행됩니다.", tags = { "ReviewController" })
	@PostMapping("/reviews")
    /* 리뷰를 달 제품 코드 , 리뷰 입력 회원번호 ,  리뷰 제목 , 리뷰 내용 */
    public ResponseEntity<ResponseDTO> insertProductReview(@RequestBody ReviewDTO reviewDTO) {

        log.info("[ReviewController] 전달 받은 reviewDTO : {} " , reviewDTO);


        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.CREATED , "리뷰 입력 성공!!" , reviewService.insertProductReview(reviewDTO)));
    }
	
	@Operation(summary = "상품 리뷰 리스트 조회 요청", description = "해당 상품에 등록된 리뷰 리스트 조회가 진행됩니다.", tags = { "ReviewController" })
    @GetMapping("/reviews/{productCode}")
    public ResponseEntity<ResponseDTO> selectReviewListWithPaging(
            @PathVariable String productCode,
            @RequestParam(name = "offset" , defaultValue = "1") String offset
            ) {
        log.info("[ReviewController] selectReviewListWithPaging : " + offset);
        log.info("[ReviewController] productCode : " + productCode);

        Criteria cri = new Criteria(Integer.valueOf(offset) , 10);
        cri.setSearchValue(productCode); // 상품을 리뷰에 대한 검색 조건으로 설정

        int total = (int) reviewService.selectReviewTotal(Integer.valueOf(cri.getSearchValue()));

        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();

        /* 1. offset 의 번호에 맞는 페이지에 뿌릴 Product 들 */
        pagingResponseDTO.setData(reviewService.selectReviewListWithPaging(cri));

        /* 2. PageDTO(Criteria(보고싶은 페이지 , 한페이지에 뿌릴 갯수) , 전체 상품 수) */
        pagingResponseDTO.setPageInfo(new PageDTO(cri , total));

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK , "조회 성공함!" , pagingResponseDTO));
    }
    
	@Operation(summary = "리뷰 상세 페이지 조회 요청", description = "해당 리뷰의 상세 페이지 조회가 진행됩니다.", tags = { "ReviewController" })
    @GetMapping("/reviews/product/{reviewCode}")
    public ResponseEntity<ResponseDTO> selectReviewDetail(@PathVariable int reviewCode) {

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK , reviewCode + "번 리뷰 상세 조회 성공" , reviewService.selectReviewDetail(reviewCode)));
    }
    
	@Operation(summary = "리뷰 수정 요청", description = "리뷰 작성자의 리뷰 수정이 진행됩니다.", tags = { "ReviewController" })
    @PutMapping("/reviews")
    /* 전달 받을 데이터 : 리뷰를 식별할 수 있는 reviewCode , reviewTitle , reviewContent   */
    public ResponseEntity<ResponseDTO> updateProductReview(@RequestBody ReviewDTO reviewDTO) {

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK , "리뷰 수정 성공" , reviewService.updateProductReview(reviewDTO)));
    }
}
