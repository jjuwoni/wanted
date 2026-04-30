package com.ohgiraffers.stability.section02.logging.service;

import com.ohgiraffers.stability.section01.exception.entity.Book;
import com.ohgiraffers.stability.section01.exception.exception.BookNotFoundException;
import com.ohgiraffers.stability.section01.exception.exception.InsufficientStockException;
import com.ohgiraffers.stability.section01.exception.repository.BookRepository;
import com.ohgiraffers.stability.section02.logging.dto.LoanDTO;
import com.ohgiraffers.stability.section02.logging.entity.Loan;
import com.ohgiraffers.stability.section02.logging.entity.Member;
import com.ohgiraffers.stability.section02.logging.repository.LoanRepository;
import com.ohgiraffers.stability.section02.logging.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 도서 대출 관리 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * <p>도서의 대출, 반납, 연장 및 연체 관리 기능을 제공한다.
 * 비즈니스 규칙을 검증하고 적절한 로깅을 수행한다.</p>
 */
@Service
@Transactional(readOnly = true)
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14; // 기본 대출 기간 14일
    private static final int MAX_LOANS_PER_MEMBER = 5; // 회원당 최대 대출 가능 도서 수

    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Autowired
    public LoanService(LoanRepository loanRepository, MemberRepository memberRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
    }

    /* 목차. 1. 대출 관련 메서드 */
    
    /**
     * 도서를 대출한다.
     * 
     * @param memberId 회원 ID
     * @param bookId 도서 ID
     * @return 생성된 대출 정보
     * @throws IllegalArgumentException 회원이나 도서가 존재하지 않거나 대출 불가능한 경우 발생
     * @throws InsufficientStockException 도서 재고가 부족한 경우 발생
     */
    @Transactional
    public LoanDTO createLoan(Long memberId, Long bookId) {
        log.info("[s2][LoanService] 도서 대출 요청 - 회원 ID: {}, 도서 ID: {}", memberId, bookId);
        
        // 회원 조회 및 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));
        
        if (!member.getActive()) {
            throw new IllegalArgumentException("비활성화된 회원은 대출할 수 없습니다: " + memberId);
        }
        
        // 도서 조회 및 검증
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        if (!book.getAvailable() || book.getStockQuantity() <= 0) {
            throw new InsufficientStockException(bookId, book.getTitle(), 1, book.getStockQuantity());
        }
        
        // 회원의 현재 대출 수 확인
        long currentLoans = loanRepository.countActiveLoansByMember(member);
        if (currentLoans >= MAX_LOANS_PER_MEMBER) {
            throw new IllegalArgumentException(
                String.format("대출 한도를 초과했습니다. 현재 대출: %d권, 최대 대출: %d권", 
                             currentLoans, MAX_LOANS_PER_MEMBER));
        }
        
        // 이미 대출 중인 도서인지 확인
        if (loanRepository.isBookCurrentlyLoaned(bookId)) {
            throw new IllegalArgumentException("이미 대출 중인 도서입니다: " + book.getTitle());
        }
        
        // 대출 생성
        Loan loan = new Loan(member, book, DEFAULT_LOAN_PERIOD_DAYS);
        Loan savedLoan = loanRepository.save(loan);
        
        // 도서 재고 감소
        book.decreaseStock(1);
        bookRepository.save(book);
        
        log.info("[s2][LoanService] 도서 대출 완료 - 대출 ID: {}, 회원: {}, 도서: {}, 반납 예정일: {}", 
                savedLoan.getLoanId(), member.getMemberName(), book.getTitle(), savedLoan.getDueDate());
        
        return convertToDTO(savedLoan);
    }

    /**
     * 도서를 반납한다.
     * 
     * @param loanId 대출 ID
     * @return 반납 처리된 대출 정보
     * @throws IllegalArgumentException 대출이 존재하지 않거나 이미 반납된 경우 발생
     */
    @Transactional
    public LoanDTO returnBook(Long loanId) {
        log.info("[s2][LoanService] 도서 반납 요청 - 대출 ID: {}", loanId);
        
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대출입니다: " + loanId));
        
        if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
            throw new IllegalArgumentException("이미 반납된 도서입니다: " + loanId);
        }
        
        // 반납 처리
        boolean isOverdue = loan.returnBook();
        Loan savedLoan = loanRepository.save(loan);
        
        // 도서 재고 증가
        Book book = loan.getBook();
        book.increaseStock(1);
        bookRepository.save(book);
        
        if (isOverdue) {
            log.warn("[s2][LoanService] 연체 반납 - 대출 ID: {}, 연체일: {}일, 연체료: {}원", 
                    loanId, loan.getOverdueDays(), loan.getFineAmount());
        } else {
            log.info("[s2][LoanService] 정상 반납 완료 - 대출 ID: {}, 회원: {}, 도서: {}", 
                    loanId, loan.getMember().getMemberName(), book.getTitle());
        }
        
        return convertToDTO(savedLoan);
    }

    /**
     * 대출을 연장한다.
     * 
     * @param loanId 대출 ID
     * @param extensionDays 연장할 일수
     * @return 연장된 대출 정보
     * @throws IllegalArgumentException 대출이 존재하지 않거나 연장 불가능한 경우 발생
     */
    @Transactional
    public LoanDTO extendLoan(Long loanId, int extensionDays) {
        log.info("[s2][LoanService] 대출 연장 요청 - 대출 ID: {}, 연장 일수: {}일", loanId, extensionDays);
        
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대출입니다: " + loanId));
        
        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new IllegalArgumentException("활성 상태의 대출만 연장할 수 있습니다: " + loanId);
        }
        
        if (extensionDays <= 0 || extensionDays > 14) {
            throw new IllegalArgumentException("연장 일수는 1일 이상 14일 이하여야 합니다: " + extensionDays);
        }
        
        LocalDateTime originalDueDate = loan.getDueDate();
        loan.extendLoan(extensionDays);
        Loan savedLoan = loanRepository.save(loan);
        
        log.info("[s2][LoanService] 대출 연장 완료 - 대출 ID: {}, 기존 반납일: {}, 새 반납일: {}", 
                loanId, originalDueDate, savedLoan.getDueDate());
        
        return convertToDTO(savedLoan);
    }

    /* 목차. 2. 조회 관련 메서드 */
    
    /**
     * 특정 회원의 대출 목록을 조회한다.
     * 
     * @param memberId 회원 ID
     * @return 해당 회원의 대출 목록
     */
    public List<LoanDTO> findLoansByMember(Long memberId) {
        log.debug("[s2][LoanService] 회원별 대출 목록 조회 - 회원 ID: {}", memberId);
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + memberId));
        
        List<Loan> loans = loanRepository.findByMember(member);
        
        log.debug("[s2][LoanService] 회원별 대출 목록 조회 완료 - 회원: {}, 대출 수: {}건", 
                member.getMemberName(), loans.size());
        
        return loans.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 연체된 대출 목록을 조회한다.
     * 
     * @return 연체된 대출 목록
     */
    public List<LoanDTO> findOverdueLoans() {
        log.debug("[s2][LoanService] 연체 대출 목록 조회 시작");
        
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDateTime.now());
        
        log.info("[s2][LoanService] 연체 대출 현황 - 총 {}건의 연체 대출 발견", overdueLoans.size());
        
        return overdueLoans.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /* 목차. 3. 연체 관리 메서드 */
    
    /**
     * 연체 대출을 일괄 처리한다.
     * 
     * @return 처리된 연체 대출 수
     */
    @Transactional
    public int processOverdueLoans() {
        log.info("[s2][LoanService] 연체 대출 일괄 처리 시작");
        
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDateTime.now());
        
        int processedCount = 0;
        for (Loan loan : overdueLoans) {
            if (loan.getStatus() == Loan.LoanStatus.ACTIVE) {
                loan.markAsOverdue();
                loanRepository.save(loan);
                processedCount++;
                
                log.warn("[s2][LoanService] 연체 처리 - 대출 ID: {}, 회원: {}, 도서: {}, 연체일: {}일", 
                        loan.getLoanId(), 
                        loan.getMember().getMemberName(), 
                        loan.getBook().getTitle(), 
                        loan.getOverdueDays());
            }
        }
        
        log.info("[s2][LoanService] 연체 대출 일괄 처리 완료 - 처리 건수: {}건", processedCount);
        
        return processedCount;
    }

    /* 목차. 4. 유틸리티 메서드 */
    
    /**
     * Loan 엔티티를 LoanDTO로 변환한다.
     * 
     * @param loan 변환할 Loan 엔티티
     * @return 변환된 LoanDTO
     */
    private LoanDTO convertToDTO(Loan loan) {
        return new LoanDTO(
                loan.getLoanId(),
                loan.getMember().getMemberId(),
                loan.getMember().getMemberName(),
                loan.getBook().getBookId(),
                loan.getBook().getTitle(),
                loan.getBook().getIsbn(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                loan.getOverdueDays(),
                loan.getFineAmount()
        );
    }
} 