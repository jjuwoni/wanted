package com.wanted.actuator.global;

import com.wanted.actuator.metric.ShopMetrics;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 예외 핸들링 시 Custom Metric 심을 준비
    private final ShopMetrics shopMetrics;

    public ApiExceptionHandler(ShopMetrics shopMetrics) {
        this.shopMetrics = shopMetrics;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException exception) {
        shopMetrics.recordApiError("bad_request");
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        shopMetrics.recordApiError("validation");
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("요청 값이 올바르지 않습니다.");

        return Map.of("message", message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception exception) {
        // 처리되지 않은 서버 오류도 제한된 reason 태그로만 기록한다.
        // exception.message나 요청별 ID를 태그로 넣지 않는다.
        shopMetrics.recordApiError("server_error");
        return Map.of("message", "서버 오류가 발생했습니다.");
    }

}
