package com.ohgiraffers.stability.section01.exception.exception;

/**
 * 도서 재고가 부족할 때 발생하는 예외
 * 
 * <p>요청한 수량보다 재고가 적을 때 발생한다.
 * 대출이나 판매 시 재고 확인 로직에서 사용된다.</p>
 */
public class InsufficientStockException extends RuntimeException {

    private final Long bookId;
    private final String bookTitle;
    private final int requestedQuantity;
    private final int availableStock;
    private final String errorCode;

    /**
     * 재고 부족 예외 생성자
     * 
     * @param bookId 도서 ID
     * @param bookTitle 도서 제목
     * @param requestedQuantity 요청 수량
     * @param availableStock 현재 재고
     */
    public InsufficientStockException(Long bookId, String bookTitle, 
                                    int requestedQuantity, int availableStock) {
        super(String.format("재고가 부족합니다. 도서: %s, 요청 수량: %d, 현재 재고: %d", 
                           bookTitle, requestedQuantity, availableStock));
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
        this.errorCode = "INSUFFICIENT_STOCK";
    }

    /**
     * 간단한 재고 부족 예외 생성자
     * 
     * @param requestedQuantity 요청 수량
     * @param availableStock 현재 재고
     */
    public InsufficientStockException(int requestedQuantity, int availableStock) {
        super(String.format("재고가 부족합니다. 요청 수량: %d, 현재 재고: %d", 
                           requestedQuantity, availableStock));
        this.bookId = null;
        this.bookTitle = null;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
        this.errorCode = "INSUFFICIENT_STOCK";
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public String getErrorCode() {
        return errorCode;
    }
} 