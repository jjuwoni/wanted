package com.ohgiraffers.jwtrestapi.purchase.controller;

import com.ohgiraffers.jwtrestapi.common.ResponseDTO;
import com.ohgiraffers.jwtrestapi.purchase.dto.PurchaseDTO;
import com.ohgiraffers.jwtrestapi.purchase.service.OrderService;
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
public class OrderController {

	private final OrderService orderService;

	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	/* 설명. @RequestBody로 넘어온 JSON 문자열을 모두 받아줄 DTO(커맨드객체)를 작성할 것(getter, setter 필수)*/
	@Operation(summary = "상품 주문 요청", description = "해당 상품 주문이 진행됩니다.", tags = { "OrderController" })
	@PostMapping("/purchase")
	public ResponseEntity<ResponseDTO> insertPurchase(@RequestBody PurchaseDTO purchaseDTO) {

		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.CREATED , "주문 성공" , orderService.insertProduct(purchaseDTO)));
	}

	

	@Operation(summary = "회원 주문 리스트 조회 요청", description = "해당 회원의 주문건에 대한 상품 리스트 조회가 진행됩니다.", tags = { "OrderController" })
	@GetMapping("/purchase/{memberId}")
    public ResponseEntity<ResponseDTO> getPurchaseList(@PathVariable String memberId) {
	
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK , "조회 성공" , orderService.selectPurchaseList(memberId)));
    }
}
