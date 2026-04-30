package com.ohgiraffers.stability.section01.exception.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 일관된 오류 응답을 위한 DTO 클래스
 * 
 * <p>모든 예외 상황에서 클라이언트에게 일관된 형태의 오류 정보를 제공한다.
 * 오류 코드, 메시지, 발생 시간, 추가 정보 등을 포함한다.</p>
 */
public class ErrorResponse {

    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, Object> details;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 기본 오류 응답 생성자
     * 
     * @param errorCode 오류 코드
     * @param message 오류 메시지
     * @param path 요청 경로
     */
    public ErrorResponse(String errorCode, String message, String path) {
        this();
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }

    /**
     * 상세 정보를 포함한 오류 응답 생성자
     * 
     * @param errorCode 오류 코드
     * @param message 오류 메시지
     * @param path 요청 경로
     * @param details 추가 상세 정보
     */
    public ErrorResponse(String errorCode, String message, String path, Map<String, Object> details) {
        this(errorCode, message, path);
        this.details = details;
    }

    // Getters and Setters
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", path='" + path + '\'' +
                ", details=" + details +
                '}';
    }
} 