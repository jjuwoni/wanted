package com.ohgiraffers.stability.section02.logging.repository;

import com.ohgiraffers.stability.section02.logging.entity.Loan;
import com.ohgiraffers.stability.section02.logging.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 대출 데이터 접근을 위한 Repository 인터페이스
 * 
 * <p>대출 엔티티에 대한 CRUD 작업과 커스텀 쿼리를 제공한다.
 * JpaRepository를 상속받아 기본적인 데이터베이스 작업을 지원한다.</p>
 */
@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /**
     * 특정 회원의 대출 목록을 조회한다.
     * 
     * @param member 회원 정보
     * @return 해당 회원의 대출 목록
     */
    List<Loan> findByMember(Member member);

    /**
     * 특정 회원의 활성 대출 목록을 조회한다.
     * 
     * @param member 회원 정보
     * @param status 대출 상태
     * @return 해당 회원의 활성 대출 목록
     */
    List<Loan> findByMemberAndStatus(Member member, Loan.LoanStatus status);

    /**
     * 특정 도서의 대출 목록을 조회한다.
     * 
     * @param bookId 도서 ID
     * @return 해당 도서의 대출 목록
     */
    @Query("SELECT l FROM Loan l WHERE l.book.bookId = :bookId")
    List<Loan> findByBookId(@Param("bookId") Long bookId);

    /**
     * 연체된 대출 목록을 조회한다.
     * 
     * @return 연체된 대출 목록
     */
    List<Loan> findByStatus(Loan.LoanStatus status);

    /**
     * 반납 예정일이 지난 활성 대출 목록을 조회한다.
     * 
     * @param currentDate 현재 날짜
     * @return 연체 대상 대출 목록
     */
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :currentDate")
    List<Loan> findOverdueLoans(@Param("currentDate") LocalDateTime currentDate);

    /**
     * 특정 기간 내의 대출 목록을 조회한다.
     * 
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간의 대출 목록
     */
    @Query("SELECT l FROM Loan l WHERE l.loanDate BETWEEN :startDate AND :endDate")
    List<Loan> findLoansBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 회원의 현재 대출 중인 도서 수를 조회한다.
     * 
     * @param member 회원 정보
     * @return 현재 대출 중인 도서 수
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.member = :member AND l.status = 'ACTIVE'")
    long countActiveLoansByMember(@Param("member") Member member);

    /**
     * 특정 도서가 현재 대출 중인지 확인한다.
     * 
     * @param bookId 도서 ID
     * @return 대출 중 여부
     */
    @Query("SELECT COUNT(l) > 0 FROM Loan l WHERE l.book.bookId = :bookId AND l.status = 'ACTIVE'")
    boolean isBookCurrentlyLoaned(@Param("bookId") Long bookId);

    /**
     * 특정 상태의 대출 수를 조회한다.
     * 
     * @param status 대출 상태
     * @return 해당 상태의 대출 수
     */
    long countByStatus(Loan.LoanStatus status);
} 