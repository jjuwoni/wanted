package com.ohgiraffers.stability.section01.exception.handler;

import com.ohgiraffers.stability.section01.exception.dto.ErrorResponse;
import com.ohgiraffers.stability.section01.exception.exception.BookNotFoundException;
import com.ohgiraffers.stability.section01.exception.exception.DuplicateIsbnException;
import com.ohgiraffers.stability.section01.exception.exception.InsufficientStockException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 * 
 * <p>애플리케이션에서 발생하는 모든 예외를 일관된 형태로 처리한다.
 * 비즈니스 예외와 시스템 예외를 구분하여 적절한 HTTP 상태 코드와 메시지를 반환한다.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* 목차. 1. 도서 관련 비즈니스 예외 처리 */
    
    /**
     * 도서를 찾을 수 없을 때 발생하는 예외를 처리한다.
     * 
     * @param ex 도서 미발견 예외
     * @param request HTTP 요청 정보
     * @return 404 상태 코드와 오류 정보
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFoundException(
            BookNotFoundException ex, HttpServletRequest request) {
        
        log.warn("[s1][GlobalExceptionHandler] 도서 조회 실패: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("searchValue", ex.getSearchValue());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                details
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * 중복 ISBN 등록 시 발생하는 예외를 처리한다.
     * 
     * @param ex 중복 ISBN 예외
     * @param request HTTP 요청 정보
     * @return 409 상태 코드와 오류 정보
     */
    @ExceptionHandler(DuplicateIsbnException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateIsbnException(
            DuplicateIsbnException ex, HttpServletRequest request) {
        
        log.warn("[s1][GlobalExceptionHandler] 중복 ISBN 등록 시도: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("isbn", ex.getIsbn());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                details
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * 재고 부족 시 발생하는 예외를 처리한다.
     * 
     * @param ex 재고 부족 예외
     * @param request HTTP 요청 정보
     * @return 400 상태 코드와 오류 정보
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(
            InsufficientStockException ex, HttpServletRequest request) {
        
        log.warn("[s1][GlobalExceptionHandler] 재고 부족: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("bookId", ex.getBookId());
        details.put("bookTitle", ex.getBookTitle());
        details.put("requestedQuantity", ex.getRequestedQuantity());
        details.put("availableStock", ex.getAvailableStock());
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                details
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /* 목차. 2. 시스템 예외 처리 */
    
    /**
     * Bean Validation 실패 시 발생하는 예외를 처리한다.
     * 
     * @param ex 검증 실패 예외
     * @param request HTTP 요청 정보
     * @return 400 상태 코드와 검증 오류 정보
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("[s1][GlobalExceptionHandler] 입력값 검증 실패: {}", ex.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            details.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_FAILED",
                "입력값 검증에 실패했습니다.",
                request.getRequestURI(),
                details
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * IllegalArgumentException을 처리한다.
     * 
     * @param ex 잘못된 인수 예외
     * @param request HTTP 요청 정보
     * @return 400 상태 코드와 오류 정보
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        log.warn("[s1][GlobalExceptionHandler] 잘못된 인수: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_ARGUMENT",
                ex.getMessage(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 예상하지 못한 모든 예외를 처리한다.
     * 
     * @param ex 예외
     * @param request HTTP 요청 정보
     * @return 500 상태 코드와 오류 정보
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("[s1][GlobalExceptionHandler] 예상하지 못한 오류 발생", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다.",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 