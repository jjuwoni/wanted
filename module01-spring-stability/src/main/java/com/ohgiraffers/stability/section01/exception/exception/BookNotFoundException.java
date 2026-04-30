package com.ohgiraffers.stability.section01.exception.exception;

/**
 * 도서를 찾을 수 없을 때 발생하는 예외
 * 
 * <p>존재하지 않는 도서 ID나 ISBN으로 조회할 때 발생한다.
 * 비즈니스 로직에서 발생하는 예외로 분류된다.</p>
 */
public class BookNotFoundException extends RuntimeException {

    private final String errorCode;
    private final Object searchValue;

    /**
     * 도서 ID로 조회 실패 시 사용하는 생성자
     * 
     * @param bookId 조회하려던 도서 ID
     */
    public BookNotFoundException(Long bookId) {
        super("해당 ID의 도서를 찾을 수 없습니다: " + bookId);
        this.errorCode = "BOOK_NOT_FOUND_BY_ID";
        this.searchValue = bookId;
    }

    /**
     * ISBN으로 조회 실패 시 사용하는 생성자
     * 
     * @param isbn 조회하려던 ISBN
     */
    public BookNotFoundException(String isbn) {
        super("해당 ISBN의 도서를 찾을 수 없습니다: " + isbn);
        this.errorCode = "BOOK_NOT_FOUND_BY_ISBN";
        this.searchValue = isbn;
    }

    /**
     * 커스텀 메시지와 함께 사용하는 생성자
     * 
     * @param message 예외 메시지
     * @param errorCode 에러 코드
     * @param searchValue 검색했던 값
     */
    public BookNotFoundException(String message, String errorCode, Object searchValue) {
        super(message);
        this.errorCode = errorCode;
        this.searchValue = searchValue;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getSearchValue() {
        return searchValue;
    }
} 