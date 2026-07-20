package com.smartlearning.lms.ai;

/**
 * FastAPI(AI 서버)가 4xx/5xx 에러를 응답했음을 나타내는 예외.
 * 상태코드와 detail을 보존해서 GlobalExceptionHandler가
 * 클라이언트에게 '그대로 이어' 전달할 수 있게 한다.
 * (FastAPI의 AppException과 대칭되는 존재)
 */
public class AiServerException extends RuntimeException {

    private final int statusCode;

    public AiServerException(int statusCode, String detail) {
        super(detail);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
