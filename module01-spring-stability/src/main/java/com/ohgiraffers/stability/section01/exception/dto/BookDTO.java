package com.ohgiraffers.stability.section01.exception.dto;

import java.time.LocalDate;

/**
 * 도서 정보 전송을 위한 DTO 클래스
 * 
 * <p>클라이언트와 서버 간 도서 정보를 주고받을 때 사용한다.
 * 엔티티와 분리하여 필요한 정보만 노출하고 데이터 전송을 최적화한다.</p>
 */
public class BookDTO {

    private Long bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Integer stockQuantity;
    private Boolean available;

    public BookDTO() {}

    public BookDTO(Long bookId, String isbn, String title, String author, 
                   String publisher, LocalDate publishDate, Integer stockQuantity, Boolean available) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.stockQuantity = stockQuantity;
        this.available = available;
    }

    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "BookDTO{" +
                "bookId=" + bookId +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishDate=" + publishDate +
                ", stockQuantity=" + stockQuantity +
                ", available=" + available +
                '}';
    }
} 