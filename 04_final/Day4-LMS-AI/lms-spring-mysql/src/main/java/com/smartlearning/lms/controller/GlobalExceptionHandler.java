package com.smartlearning.lms.controller;

import com.smartlearning.lms.ai.AiServerException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * [Day 4 교체본] 에러 응답 계약 v1.1: {"detail": ..., "request_id": ...}
 *
 * request_id를 에러 본문에 넣는 이유:
 *   Postman에서 에러를 받은 사람이 그 ID로 Spring/FastAPI 양쪽 로그를
 *   즉시 찾아갈 수 있다. "에러 났어요"가 아니라 "rid=a1b2c3d4 에러예요"
 *   라고 제보받는 순간 장애 분석 시간이 10분의 1이 된다.
 *
 * FastAPI의 core/exceptions.py와 같은 원칙으로 만들었다:
 *   예상된 실패 = WARN (스택트레이스 없음) / 버그 = ERROR (스택트레이스는 로그에만)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String, Object> body(Object detail) {
        return Map.of("detail", detail,
                      "request_id", String.valueOf(MDC.get("requestId")));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException e) {
        log.warn("처리된 예외 404: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(e.getMessage()));
    }

    /**
     * FastAPI(AI 서버)가 에러를 응답한 경우 — 상태코드와 detail을
     * '그대로 이어서' 클라이언트에게 전달한다.
     * 예: FastAPI 404 "수강 이력이 없어 추천할 수 없습니다"
     *     → Spring도 404 + 같은 detail (+ 같은 rid!)
     * 두 서버의 예외가 '한 줄로 연결'되는 지점이다.
     */
    @ExceptionHandler(AiServerException.class)
    public ResponseEntity<Map<String, Object>> handleAiServer(AiServerException e) {
        log.warn("AI 서버 오류 전달 {}: {}", e.getStatusCode(), e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(body(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception e) {
        log.error("처리되지 않은 예외", e);   // 스택트레이스는 로그에만!
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body("서버 내부 오류가 발생했습니다."));
    }
}
