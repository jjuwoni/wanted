package com.ohgiraffers.jwtrestapi.exception.dto;

import org.springframework.http.HttpStatus;

/* 설명. 예외 발생 시 프론트로 보낼 데이터를 담는 DTO 객체 */
public class ApiExceptionDTO {

    private int status;			// 상태 코드
    private String message; 	// 에러 메세지

    public ApiExceptionDTO() {
    }

    public ApiExceptionDTO(HttpStatus status, String message) {		// 첫번째 매개변수 int 아니니까 조심하자.
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiExceptionDTO [status=" + status + ", message=" + message + "]";
    }

}