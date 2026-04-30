package com.ohgiraffers.stability.section02.logging.dto;

import com.ohgiraffers.stability.section02.logging.entity.Loan;
import java.time.LocalDateTime;

/**
 * 대출 정보 전송을 위한 DTO 클래스
 * 
 * <p>클라이언트와 서버 간 대출 정보를 주고받을 때 사용한다.
 * 엔티티와 분리하여 필요한 정보만 노출하고 데이터 전송을 최적화한다.</p>
 */
public class LoanDTO {

    private Long loanId;
    private Long memberId;
    private String memberName;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private Loan.LoanStatus status;
    private Integer overdueDays;
    private Integer fineAmount;

    public LoanDTO() {}

    public LoanDTO(Long loanId, Long memberId, String memberName, Long bookId, String bookTitle, 
                   String bookIsbn, LocalDateTime loanDate, LocalDateTime dueDate, 
                   LocalDateTime returnDate, Loan.LoanStatus status, Integer overdueDays, Integer fineAmount) {
        this.loanId = loanId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookIsbn = bookIsbn;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.overdueDays = overdueDays;
        this.fineAmount = fineAmount;
    }

    // Getters and Setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Loan.LoanStatus getStatus() {
        return status;
    }

    public void setStatus(Loan.LoanStatus status) {
        this.status = status;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public Integer getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Integer fineAmount) {
        this.fineAmount = fineAmount;
    }

    @Override
    public String toString() {
        return "LoanDTO{" +
                "loanId=" + loanId +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", bookIsbn='" + bookIsbn + '\'' +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status=" + status +
                ", overdueDays=" + overdueDays +
                ", fineAmount=" + fineAmount +
                '}';
    }
} 