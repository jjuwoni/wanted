package com.ohgiraffers.jwtrestapi.member.controller;

import com.ohgiraffers.jwtrestapi.common.ResponseDTO;
import com.ohgiraffers.jwtrestapi.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MemberController {
	
	private final MemberService memberService;
	
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@Operation(summary = "회원 조회 요청", description = "회원 한명이 조회됩니다.", tags = { "MemberController" })
	@GetMapping("/members/{memberId}")
	public ResponseEntity<ResponseDTO> selectMyMemberInfo(@PathVariable String memberId) {
		return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "조회 성공", memberService.selectMyInfo(memberId)));
	}
}
