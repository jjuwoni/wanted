package com.ohgiraffers.stability.section01.exception.exception;

/**
 * 중복된 ISBN으로 도서 등록 시 발생하는 예외
 * 
 * <p>이미 존재하는 ISBN으로 새로운 도서를 등록하려 할 때 발생한다.
 * 데이터 무결성을 보장하기 위한 비즈니스 예외이다.</p>
 */
public class DuplicateIsbnException extends RuntimeException {

    private final String isbn;
    private final String errorCode;

    /**
     * 중복 ISBN 예외 생성자
     * 
     * @param isbn 중복된 ISBN
     */
    public DuplicateIsbnException(String isbn) {
        super("이미 존재하는 ISBN입니다: " + isbn);
        this.isbn = isbn;
        this.errorCode = "DUPLICATE_ISBN";
    }

    /**
     * 커스텀 메시지와 함께 사용하는 생성자
     * 
     * @param isbn 중복된 ISBN
     * @param message 예외 메시지
     */
    public DuplicateIsbnException(String isbn, String message) {
        super(message);
        this.isbn = isbn;
        this.errorCode = "DUPLICATE_ISBN";
    }

    public String getIsbn() {
        return isbn;
    }

    public String getErrorCode() {
        return errorCode;
    }
} 