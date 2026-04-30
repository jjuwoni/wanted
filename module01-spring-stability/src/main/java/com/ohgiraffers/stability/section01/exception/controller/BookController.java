package com.ohgiraffers.stability.section01.exception.controller;

import com.ohgiraffers.stability.section01.exception.dto.BookDTO;
import com.ohgiraffers.stability.section01.exception.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 도서 관리 REST API 컨트롤러
 * 
 * <p>도서 관련 HTTP 요청을 처리하고 적절한 응답을 반환한다.
 * 예외 상황은 GlobalExceptionHandler에서 일관되게 처리된다.</p>
 */
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /* 목차. 1. 도서 조회 API */
    
    /**
     * 모든 도서 목록을 조회한다.
     * 
     * @return 전체 도서 목록
     */
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        log.info("[s1][BookController] 전체 도서 목록 조회 요청");
        
        List<BookDTO> books = bookService.findAllBooks();
        
        log.info("[s1][BookController] 전체 도서 목록 조회 완료 - 총 {}권", books.size());
        return ResponseEntity.ok(books);
    }

    /**
     * 도서 ID로 단일 도서를 조회한다.
     * 
     * @param bookId 조회할 도서 ID
     * @return 조회된 도서 정보
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long bookId) {
        log.info("[s1][BookController] 도서 조회 요청 - ID: {}", bookId);
        
        BookDTO book = bookService.findBookById(bookId);
        
        log.info("[s1][BookController] 도서 조회 완료 - ID: {}, 제목: {}", bookId, book.getTitle());
        return ResponseEntity.ok(book);
    }

    /**
     * ISBN으로 도서를 조회한다.
     * 
     * @param isbn 조회할 ISBN
     * @return 조회된 도서 정보
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {
        log.info("[s1][BookController] ISBN으로 도서 조회 요청 - ISBN: {}", isbn);
        
        BookDTO book = bookService.findBookByIsbn(isbn);
        
        log.info("[s1][BookController] ISBN으로 도서 조회 완료 - ISBN: {}, 제목: {}", isbn, book.getTitle());
        return ResponseEntity.ok(book);
    }

    /**
     * 제목으로 도서를 검색한다.
     * 
     * @param keyword 검색 키워드
     * @return 검색된 도서 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooksByTitle(@RequestParam String keyword) {
        log.info("[s1][BookController] 제목으로 도서 검색 요청 - 키워드: {}", keyword);
        
        List<BookDTO> books = bookService.searchBooksByTitle(keyword);
        
        log.info("[s1][BookController] 제목으로 도서 검색 완료 - 키워드: {}, 검색 결과: {}권", keyword, books.size());
        return ResponseEntity.ok(books);
    }

    /* 목차. 2. 도서 등록 및 수정 API */
    
    /**
     * 새로운 도서를 등록한다.
     * 
     * @param bookDTO 등록할 도서 정보
     * @return 등록된 도서 정보
     */
    @PostMapping
    public ResponseEntity<BookDTO> registerBook(@RequestBody BookDTO bookDTO) {
        log.info("[s1][BookController] 도서 등록 요청 - ISBN: {}, 제목: {}", bookDTO.getIsbn(), bookDTO.getTitle());
        
        BookDTO registeredBook = bookService.registerBook(bookDTO);
        
        log.info("[s1][BookController] 도서 등록 완료 - ID: {}, 제목: {}", registeredBook.getBookId(), registeredBook.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredBook);
    }

    /**
     * 도서 정보를 수정한다.
     * 
     * @param bookId 수정할 도서 ID
     * @param bookDTO 수정할 도서 정보
     * @return 수정된 도서 정보
     */
    @PutMapping("/{bookId}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long bookId, @RequestBody BookDTO bookDTO) {
        log.info("[s1][BookController] 도서 정보 수정 요청 - ID: {}", bookId);
        
        BookDTO updatedBook = bookService.updateBook(bookId, bookDTO);
        
        log.info("[s1][BookController] 도서 정보 수정 완료 - ID: {}, 제목: {}", updatedBook.getBookId(), updatedBook.getTitle());
        return ResponseEntity.ok(updatedBook);
    }

    /* 목차. 3. 재고 관리 API */
    
    /**
     * 도서 재고를 증가시킨다.
     * 
     * @param bookId 도서 ID
     * @param quantity 증가시킬 수량
     * @return 업데이트된 도서 정보
     */
    @PatchMapping("/{bookId}/stock/increase")
    public ResponseEntity<BookDTO> increaseStock(@PathVariable Long bookId, @RequestParam int quantity) {
        log.info("[s1][BookController] 재고 증가 요청 - 도서 ID: {}, 증가 수량: {}", bookId, quantity);
        
        BookDTO updatedBook = bookService.increaseStock(bookId, quantity);
        
        log.info("[s1][BookController] 재고 증가 완료 - 도서 ID: {}, 현재 재고: {}", bookId, updatedBook.getStockQuantity());
        return ResponseEntity.ok(updatedBook);
    }

    /**
     * 도서 재고를 감소시킨다.
     * 
     * @param bookId 도서 ID
     * @param quantity 감소시킬 수량
     * @return 업데이트된 도서 정보
     */
    @PatchMapping("/{bookId}/stock/decrease")
    public ResponseEntity<BookDTO> decreaseStock(@PathVariable Long bookId, @RequestParam int quantity) {
        log.info("[s1][BookController] 재고 감소 요청 - 도서 ID: {}, 감소 수량: {}", bookId, quantity);
        
        BookDTO updatedBook = bookService.decreaseStock(bookId, quantity);
        
        log.info("[s1][BookController] 재고 감소 완료 - 도서 ID: {}, 현재 재고: {}", bookId, updatedBook.getStockQuantity());
        return ResponseEntity.ok(updatedBook);
    }

    /* 목차. 4. 도서 삭제 API */
    
    /**
     * 도서를 삭제한다.
     * 
     * @param bookId 삭제할 도서 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        log.info("[s1][BookController] 도서 삭제 요청 - ID: {}", bookId);
        
        bookService.deleteBook(bookId);
        
        log.info("[s1][BookController] 도서 삭제 완료 - ID: {}", bookId);
        return ResponseEntity.noContent().build();
    }
} 