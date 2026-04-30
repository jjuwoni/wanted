package com.ohgiraffers.stability.section01.exception.service;

import com.ohgiraffers.stability.section01.exception.dto.BookDTO;
import com.ohgiraffers.stability.section01.exception.entity.Book;
import com.ohgiraffers.stability.section01.exception.exception.BookNotFoundException;
import com.ohgiraffers.stability.section01.exception.exception.DuplicateIsbnException;
import com.ohgiraffers.stability.section01.exception.exception.InsufficientStockException;
import com.ohgiraffers.stability.section01.exception.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 도서 관리 비즈니스 로직을 처리하는 서비스 클래스
 * 
 * <p>도서의 등록, 조회, 수정, 삭제 및 재고 관리 기능을 제공한다.
 * 비즈니스 규칙을 검증하고 적절한 예외를 발생시킨다.</p>
 */
@Service
@Transactional(readOnly = true)
public class BookService {

        private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /* 목차. 1. 도서 조회 관련 메서드 */
    
    /**
     * 도서 ID로 단일 도서를 조회한다.
     * 
     * @param bookId 조회할 도서 ID
     * @return 조회된 도서 정보
     * @throws BookNotFoundException 해당 ID의 도서가 존재하지 않을 경우 발생
     */
    public BookDTO findBookById(Long bookId) {
        log.debug("도서 조회 시작 - ID: {}", bookId);
        
        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));
            
            log.debug("도서 조회 완료 - 제목: {}", book.getTitle());
            return convertToDTO(book);
        } catch (BookNotFoundException e) {
            log.error("도서 조회 실패 - ID: {}: {}", bookId, e.getMessage());
            throw e;
        }
    }

    /**
     * ISBN으로 도서를 조회한다.
     * 
     * @param isbn 조회할 ISBN
     * @return 조회된 도서 정보
     * @throws BookNotFoundException 해당 ISBN의 도서가 존재하지 않을 경우 발생
     */
    public BookDTO findBookByIsbn(String isbn) {
        log.debug("ISBN으로 도서 조회 시작 - ISBN: {}", isbn);
        
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
        
        log.debug("ISBN으로 도서 조회 완료 - 제목: {}", book.getTitle());
        return convertToDTO(book);
    }

    /**
     * 모든 도서 목록을 조회한다.
     * 
     * @return 전체 도서 목록
     */
    public List<BookDTO> findAllBooks() {
        log.debug("전체 도서 목록 조회 시작");
        
        try {
            List<Book> books = bookRepository.findAll();
            
            log.debug("전체 도서 목록 조회 완료 - 총 {}권", books.size());
            return books.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } finally {
            log.debug("전체 도서 목록 조회 종료");
        }
    }

    /**
     * 제목으로 도서를 검색한다.
     * 
     * @param keyword 검색 키워드
     * @return 검색된 도서 목록
     */
    public List<BookDTO> searchBooksByTitle(String keyword) {
        log.debug("제목으로 도서 검색 시작 - 키워드: {}", keyword);
        
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(keyword);
        
        log.debug("제목으로 도서 검색 완료 - 검색 결과: {}권", books.size());
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /* 목차. 2. 도서 등록 및 수정 관련 메서드 */
    
    /**
     * 새로운 도서를 등록한다.
     * 
     * @param bookDTO 등록할 도서 정보
     * @return 등록된 도서 정보
     * @throws DuplicateIsbnException 이미 존재하는 ISBN인 경우 발생
     */
    @Transactional
    public BookDTO registerBook(BookDTO bookDTO) {
        log.debug("도서 등록 시작 - ISBN: {}, 제목: {}", bookDTO.getIsbn(), bookDTO.getTitle());
        
        // ISBN 중복 검사
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateIsbnException(bookDTO.getIsbn());
        }
        
        Book book = new Book(
                bookDTO.getIsbn(),
                bookDTO.getTitle(),
                bookDTO.getAuthor(),
                bookDTO.getPublisher(),
                bookDTO.getPublishDate(),
                bookDTO.getStockQuantity()
        );
        
        Book savedBook = bookRepository.save(book);
        
        log.info("[s1][BookService] 도서 등록 완료 - ID: {}, 제목: {}", savedBook.getBookId(), savedBook.getTitle());
        return convertToDTO(savedBook);
    }

    /**
     * 도서 정보를 수정한다.
     * 
     * @param bookId 수정할 도서 ID
     * @param bookDTO 수정할 도서 정보
     * @return 수정된 도서 정보
     * @throws BookNotFoundException 해당 ID의 도서가 존재하지 않을 경우 발생
     */
    @Transactional
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) {
        log.debug("도서 정보 수정 시작 - ID: {}", bookId);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        // 도서 정보 업데이트
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setPublisher(bookDTO.getPublisher());
        book.setPublishDate(bookDTO.getPublishDate());
        
        Book updatedBook = bookRepository.save(book);
        
        log.info("[s1][BookService] 도서 정보 수정 완료 - ID: {}, 제목: {}", updatedBook.getBookId(), updatedBook.getTitle());
        return convertToDTO(updatedBook);
    }

    /* 목차. 3. 재고 관리 관련 메서드 */
    
    /**
     * 도서 재고를 증가시킨다.
     * 
     * @param bookId 도서 ID
     * @param quantity 증가시킬 수량
     * @return 업데이트된 도서 정보
     * @throws BookNotFoundException 해당 ID의 도서가 존재하지 않을 경우 발생
     */
    @Transactional
    public BookDTO increaseStock(Long bookId, int quantity) {
        log.debug("재고 증가 시작 - 도서 ID: {}, 증가 수량: {}", bookId, quantity);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        book.increaseStock(quantity);
        Book updatedBook = bookRepository.save(book);
        
        log.info("[s1][BookService] 재고 증가 완료 - 도서 ID: {}, 현재 재고: {}", bookId, updatedBook.getStockQuantity());
        return convertToDTO(updatedBook);
    }

    /**
     * 도서 재고를 감소시킨다.
     * 
     * @param bookId 도서 ID
     * @param quantity 감소시킬 수량
     * @return 업데이트된 도서 정보
     * @throws BookNotFoundException 해당 ID의 도서가 존재하지 않을 경우 발생
     * @throws InsufficientStockException 재고가 부족한 경우 발생
     */
    @Transactional
    public BookDTO decreaseStock(Long bookId, int quantity) {
        log.debug("재고 감소 시작 - 도서 ID: {}, 감소 수량: {}", bookId, quantity);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        // 재고 부족 검사
        if (book.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    book.getBookId(), 
                    book.getTitle(), 
                    quantity, 
                    book.getStockQuantity()
            );
        }
        
        book.decreaseStock(quantity);
        Book updatedBook = bookRepository.save(book);
        
        log.info("[s1][BookService] 재고 감소 완료 - 도서 ID: {}, 현재 재고: {}", bookId, updatedBook.getStockQuantity());
        return convertToDTO(updatedBook);
    }

    /* 목차. 4. 도서 삭제 관련 메서드 */
    
    /**
     * 도서를 삭제한다.
     * 
     * @param bookId 삭제할 도서 ID
     * @throws BookNotFoundException 해당 ID의 도서가 존재하지 않을 경우 발생
     */
    @Transactional
    public void deleteBook(Long bookId) {
        log.debug("도서 삭제 시작 - ID: {}", bookId);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        bookRepository.delete(book);
        
        log.info("[s1][BookService] 도서 삭제 완료 - ID: {}, 제목: {}", bookId, book.getTitle());
    }

    /* 목차. 5. 유틸리티 메서드 */
    
    /**
     * Book 엔티티를 BookDTO로 변환한다.
     * 
     * @param book 변환할 Book 엔티티
     * @return 변환된 BookDTO
     */
    private BookDTO convertToDTO(Book book) {
        return new BookDTO(
                book.getBookId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishDate(),
                book.getStockQuantity(),
                book.getAvailable()
        );
    }
} 