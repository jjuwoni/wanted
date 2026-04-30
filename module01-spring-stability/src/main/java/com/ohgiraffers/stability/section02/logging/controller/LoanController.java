package com.ohgiraffers.stability.section02.logging.controller;

import com.ohgiraffers.stability.section02.logging.dto.LoanDTO;
import com.ohgiraffers.stability.section02.logging.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 도서 대출 관리 REST API 컨트롤러
 * 
 * <p>도서 대출 관련 HTTP 요청을 처리하고 적절한 응답을 반환한다.
 * 모든 요청과 응답은 LoggingAspect에서 자동으로 로깅된다.</p>
 */
@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private static final Logger log = LoggerFactory.getLogger(LoanController.class);
    
    private final LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /* 목차. 1. 대출 관리 API */
    
    /**
     * 도서를 대출한다.
     * 
     * @param memberId 회원 ID
     * @param bookId 도서 ID
     * @return 생성된 대출 정보
     */
    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestParam Long memberId, @RequestParam Long bookId) {
        log.info("[s2][LoanController] 대출 요청 접수 - 회원 ID: {}, 도서 ID: {}", memberId, bookId);
        
        LoanDTO loan = loanService.createLoan(memberId, bookId);
        
        log.info("[s2][LoanController] 대출 요청 처리 완료 - 대출 ID: {}", loan.getLoanId());
        return ResponseEntity.status(HttpStatus.CREATED).body(loan);
    }

    /**
     * 도서를 반납한다.
     * 
     * @param loanId 대출 ID
     * @return 반납 처리된 대출 정보
     */
    @PatchMapping("/{loanId}/return")
    public ResponseEntity<LoanDTO> returnBook(@PathVariable Long loanId) {
        log.info("[s2][LoanController] 반납 요청 접수 - 대출 ID: {}", loanId);
        
        LoanDTO loan = loanService.returnBook(loanId);
        
        log.info("[s2][LoanController] 반납 요청 처리 완료 - 대출 ID: {}", loanId);
        return ResponseEntity.ok(loan);
    }

    /**
     * 대출을 연장한다.
     * 
     * @param loanId 대출 ID
     * @param extensionDays 연장할 일수
     * @return 연장된 대출 정보
     */
    @PatchMapping("/{loanId}/extend")
    public ResponseEntity<LoanDTO> extendLoan(@PathVariable Long loanId, @RequestParam int extensionDays) {
        log.info("[s2][LoanController] 대출 연장 요청 접수 - 대출 ID: {}, 연장 일수: {}일", loanId, extensionDays);
        
        LoanDTO loan = loanService.extendLoan(loanId, extensionDays);
        
        log.info("[s2][LoanController] 대출 연장 요청 처리 완료 - 대출 ID: {}", loanId);
        return ResponseEntity.ok(loan);
    }

    /* 목차. 2. 대출 조회 API */
    
    /**
     * 특정 회원의 대출 목록을 조회한다.
     * 
     * @param memberId 회원 ID
     * @return 해당 회원의 대출 목록
     */
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<LoanDTO>> getLoansByMember(@PathVariable Long memberId) {
        log.info("[s2][LoanController] 회원별 대출 목록 조회 요청 - 회원 ID: {}", memberId);
        
        List<LoanDTO> loans = loanService.findLoansByMember(memberId);
        
        log.info("[s2][LoanController] 회원별 대출 목록 조회 완료 - 회원 ID: {}, 대출 수: {}건", memberId, loans.size());
        return ResponseEntity.ok(loans);
    }

    /**
     * 연체된 대출 목록을 조회한다.
     * 
     * @return 연체된 대출 목록
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<LoanDTO>> getOverdueLoans() {
        log.info("[s2][LoanController] 연체 대출 목록 조회 요청");
        
        List<LoanDTO> overdueLoans = loanService.findOverdueLoans();
        
        log.info("[s2][LoanController] 연체 대출 목록 조회 완료 - 연체 건수: {}건", overdueLoans.size());
        return ResponseEntity.ok(overdueLoans);
    }

    /* 목차. 3. 관리자 API */
    
    /**
     * 연체 대출을 일괄 처리한다.
     * 
     * @return 처리 결과
     */
    @PostMapping("/overdue/process")
    public ResponseEntity<Map<String, Object>> processOverdueLoans() {
        log.info("[s2][LoanController] 연체 대출 일괄 처리 요청");
        
        int processedCount = loanService.processOverdueLoans();
        
        Map<String, Object> response = Map.of(
                "message", "연체 대출 일괄 처리가 완료되었습니다.",
                "processedCount", processedCount
        );
        
        log.info("[s2][LoanController] 연체 대출 일괄 처리 완료 - 처리 건수: {}건", processedCount);
        return ResponseEntity.ok(response);
    }

    /* 목차. 4. 시스템 상태 확인 API */
    
    /**
     * 대출 시스템 상태를 확인한다.
     * 
     * @return 시스템 상태 정보
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        log.info("[s2][LoanController] 대출 시스템 상태 확인 요청");
        
        // 간단한 시스템 상태 정보 반환
        Map<String, Object> status = Map.of(
                "status", "HEALTHY",
                "timestamp", java.time.LocalDateTime.now(),
                "message", "대출 시스템이 정상적으로 동작 중입니다."
        );
        
        log.info("[s2][LoanController] 대출 시스템 상태 확인 완료 - 상태: HEALTHY");
        return ResponseEntity.ok(status);
    }

    /**
     * 로깅 테스트를 위한 API
     * 
     * @param level 로그 레벨 (debug, info, warn, error)
     * @param message 로그 메시지
     * @return 테스트 결과
     */
    @PostMapping("/test/logging")
    public ResponseEntity<Map<String, String>> testLogging(@RequestParam String level, @RequestParam String message) {
        log.info("[s2][LoanController] 로깅 테스트 요청 - 레벨: {}, 메시지: {}", level, message);
        
        // 요청된 레벨에 따라 로그 출력
        switch (level.toLowerCase()) {
            case "debug":
                log.debug("[s2][LoanController] DEBUG 테스트: {}", message);
                break;
            case "info":
                log.info("[s2][LoanController] INFO 테스트: {}", message);
                break;
            case "warn":
                log.warn("[s2][LoanController] WARN 테스트: {}", message);
                break;
            case "error":
                log.error("[s2][LoanController] ERROR 테스트: {}", message);
                break;
            default:
                log.info("[s2][LoanController] 기본 INFO 테스트: {}", message);
        }
        
        Map<String, String> response = Map.of(
                "result", "로깅 테스트가 완료되었습니다.",
                "level", level,
                "message", message
        );
        
        return ResponseEntity.ok(response);
    }
} 