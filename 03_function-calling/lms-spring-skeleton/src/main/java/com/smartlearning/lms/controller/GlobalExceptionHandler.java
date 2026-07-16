package com.smartlearning.lms.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 에러 응답을 {"detail": "..."} 형식으로 통일한다.
 *
 * 왜 이 형식인가? FastAPI의 기본 에러 형식이 {"detail": ...}이다.
 * 양쪽 서버의 에러 형식을 하나로 맞추면(API 계약서 2장),
 * 호출하는 쪽(FastAPI의 httpx, 브라우저 JS)이 에러 파싱 코드를
 * 한 벌만 갖고 있으면 된다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(EntityNotFoundException e) {
        return Map.of("detail", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnexpected(Exception e) {
        return Map.of("detail", "서버 내부 오류: " + e.getMessage());
    }
}
