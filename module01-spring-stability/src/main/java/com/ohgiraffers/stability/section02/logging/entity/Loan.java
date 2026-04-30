package com.ohgiraffers.stability.section02.logging.entity;

import com.ohgiraffers.stability.section01.exception.entity.Book;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 도서 대출 정보를 관리하는 엔티티 클래스
 * 
 * <p>도서관 시스템에서 도서 대출 및 반납 정보를 저장하고 관리한다.
 * 대출일, 반납 예정일, 실제 반납일, 연체 여부 등의 정보를 포함한다.</p>
 */
@Entity
@Table(name = "tbl_loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id")
    private Long loanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "loan_date", nullable = false)
    private LocalDateTime loanDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @Column(name = "overdue_days")
    private Integer overdueDays;

    @Column(name = "fine_amount")
    private Integer fineAmount;

    protected Loan() {}

    public Loan(Member member, Book book, int loanPeriodDays) {
        this.member = member;
        this.book = book;
        this.loanDate = LocalDateTime.now();
        this.dueDate = this.loanDate.plusDays(loanPeriodDays);
        this.status = LoanStatus.ACTIVE;
        this.overdueDays = 0;
        this.fineAmount = 0;
    }

    // Getters
    public Long getLoanId() {
        return loanId;
    }

    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public Integer getFineAmount() {
        return fineAmount;
    }

    // Setters
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public void setFineAmount(Integer fineAmount) {
        this.fineAmount = fineAmount;
    }

    /**
     * 도서를 반납 처리한다.
     * 
     * @return 연체 여부
     */
    public boolean returnBook() {
        this.returnDate = LocalDateTime.now();
        this.status = LoanStatus.RETURNED;
        
        // 연체 계산
        if (this.returnDate.isAfter(this.dueDate)) {
            this.overdueDays = (int) java.time.Duration.between(this.dueDate, this.returnDate).toDays();
            this.fineAmount = this.overdueDays * 100; // 하루당 100원 연체료
            return true; // 연체
        }
        
        return false; // 정상 반납
    }

    /**
     * 대출을 연장한다.
     * 
     * @param extensionDays 연장할 일수
     */
    public void extendLoan(int extensionDays) {
        if (this.status == LoanStatus.ACTIVE) {
            this.dueDate = this.dueDate.plusDays(extensionDays);
        }
    }

    /**
     * 연체 상태로 변경한다.
     */
    public void markAsOverdue() {
        if (this.status == LoanStatus.ACTIVE && LocalDateTime.now().isAfter(this.dueDate)) {
            this.status = LoanStatus.OVERDUE;
            this.overdueDays = (int) java.time.Duration.between(this.dueDate, LocalDateTime.now()).toDays();
            this.fineAmount = this.overdueDays * 100;
        }
    }

    /**
     * 대출 상태를 나타내는 열거형
     */
    public enum LoanStatus {
        ACTIVE,    // 대출 중
        RETURNED,  // 반납 완료
        OVERDUE    // 연체
    }
} 