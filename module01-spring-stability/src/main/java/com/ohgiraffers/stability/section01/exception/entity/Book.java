package com.ohgiraffers.stability.section01.exception.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 도서 정보를 관리하는 엔티티 클래스
 * 
 * <p>도서관 시스템에서 도서의 기본 정보를 저장하고 관리한다.
 * ISBN, 제목, 저자, 출판사, 출간일, 재고 수량 등의 정보를 포함한다.</p>
 */
@Entity
@Table(name = "tbl_book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "isbn", unique = true, nullable = false, length = 13)
    private String isbn;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "publisher", nullable = false, length = 100)
    private String publisher;

    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "available", nullable = false)
    private Boolean available;

    protected Book() {}

    public Book(String isbn, String title, String author, String publisher, 
                LocalDate publishDate, Integer stockQuantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.stockQuantity = stockQuantity;
        this.available = true;
    }

    // Getters
    public Long getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublisher() {
        return publisher;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Boolean getAvailable() {
        return available;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * 도서 재고를 감소시킨다.
     * 
     * @param quantity 감소시킬 수량
     * @throws IllegalArgumentException 재고가 부족한 경우 발생
     */
    public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.stockQuantity);
        }
        this.stockQuantity -= quantity;
        
        if (this.stockQuantity == 0) {
            this.available = false;
        }
    }

    /**
     * 도서 재고를 증가시킨다.
     * 
     * @param quantity 증가시킬 수량
     */
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
        if (this.stockQuantity > 0) {
            this.available = true;
        }
    }
} 