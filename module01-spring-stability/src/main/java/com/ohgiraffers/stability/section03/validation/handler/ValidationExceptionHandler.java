package com.ohgiraffers.stability.section03.validation.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bean Validation 관련 예외를 처리하는 전역 예외 핸들러
 * 
 * <p>다양한 유형의 검증 예외를 포착하여 일관된 형태의 오류 응답을 생성한다.
 * 클라이언트가 검증 오류를 쉽게 이해할 수 있도록 상세한 오류 정보를 제공한다.</p>
 */
@RestControllerAdvice(basePackages = "com.ohgiraffers.stability.section03.validation")
public class ValidationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    /**
     * @Valid 어노테이션으로 인한 검증 실패를 처리한다.
     * 주로 @RequestBody에서 발생하는 검증 오류를 처리한다.
     * 
     * @param ex MethodArgumentNotValidException
     * @return 검증 오류 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("[s3][ValidationExceptionHandler] Bean Validation 실패 - @RequestBody 검증 오류: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        
        // 필드별 오류 메시지 수집
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                fieldErrors.put("global", error.getDefaultMessage());
            }
        });
        
        Map<String, Object> errorResponse = createErrorResponse(
                "VALIDATION_FAILED",
                "입력 데이터 검증에 실패했습니다.",
                fieldErrors
        );
        
        log.warn("[s3][ValidationExceptionHandler] Bean Validation 실패 상세 - 필드 오류: {}", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * @Validated 어노테이션으로 인한 검증 실패를 처리한다.
     * 주로 @PathVariable, @RequestParam에서 발생하는 검증 오류를 처리한다.
     * 
     * @param ex ConstraintViolationException
     * @return 검증 오류 응답
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("[s3][ValidationExceptionHandler] Constraint Validation 실패 - 매개변수 검증 오류: {}", ex.getMessage());
        
        Map<String, String> violations = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> getPropertyPath(violation),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing // 중복 키가 있을 경우 기존 값 유지
                ));
        
        Map<String, Object> errorResponse = createErrorResponse(
                "CONSTRAINT_VIOLATION",
                "매개변수 검증에 실패했습니다.",
                violations
        );
        
        log.warn("[s3][ValidationExceptionHandler] Constraint Validation 실패 상세 - 제약 조건 위반: {}", violations);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 일반적인 IllegalArgumentException을 처리한다.
     * 비즈니스 로직에서 발생하는 검증 오류를 처리한다.
     * 
     * @param ex IllegalArgumentException
     * @return 검증 오류 응답
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[s3][ValidationExceptionHandler] 비즈니스 검증 실패 - {}", ex.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
                "BUSINESS_VALIDATION_FAILED",
                ex.getMessage(),
                null
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 일반적인 RuntimeException을 처리한다.
     * 예상하지 못한 검증 관련 오류를 처리한다.
     * 
     * @param ex RuntimeException
     * @return 서버 오류 응답
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("[s3][ValidationExceptionHandler] 예상하지 못한 검증 오류 발생", ex);
        
        Map<String, Object> errorResponse = createErrorResponse(
                "INTERNAL_VALIDATION_ERROR",
                "검증 처리 중 내부 오류가 발생했습니다.",
                null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 표준화된 오류 응답을 생성한다.
     * 
     * @param errorCode 오류 코드
     * @param message 오류 메시지
     * @param details 상세 오류 정보
     * @return 오류 응답 맵
     */
    private Map<String, Object> createErrorResponse(String errorCode, String message, Map<String, String> details) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now());
        
        if (details != null && !details.isEmpty()) {
            errorResponse.put("details", details);
        }
        
        return errorResponse;
    }

    /**
     * ConstraintViolation에서 속성 경로를 추출한다.
     * 
     * @param violation 제약 조건 위반 정보
     * @return 속성 경로 문자열
     */
    private String getPropertyPath(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        
        // 메서드 매개변수의 경우 마지막 부분만 추출
        if (propertyPath.contains(".")) {
            String[] parts = propertyPath.split("\\.");
            return parts[parts.length - 1];
        }
        
        return propertyPath;
    }
} 