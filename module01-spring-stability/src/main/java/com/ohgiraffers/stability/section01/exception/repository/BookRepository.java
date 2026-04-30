package com.ohgiraffers.stability.section01.exception.repository;

import com.ohgiraffers.stability.section01.exception.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 도서 데이터 접근을 위한 Repository 인터페이스
 * 
 * <p>도서 엔티티에 대한 CRUD 작업과 커스텀 쿼리를 제공한다.
 * JpaRepository를 상속받아 기본적인 데이터베이스 작업을 지원한다.</p>
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * ISBN으로 도서를 조회한다.
     * 
     * @param isbn 조회할 ISBN
     * @return 조회된 도서 정보 (Optional)
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * ISBN 존재 여부를 확인한다.
     * 
     * @param isbn 확인할 ISBN
     * @return 존재 여부
     */
    boolean existsByIsbn(String isbn);

    /**
     * 제목에 특정 키워드가 포함된 도서 목록을 조회한다.
     * 
     * @param keyword 검색 키워드
     * @return 검색된 도서 목록
     */
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    /**
     * 저자명으로 도서 목록을 조회한다.
     * 
     * @param author 저자명
     * @return 해당 저자의 도서 목록
     */
    List<Book> findByAuthorContainingIgnoreCase(String author);

    /**
     * 대출 가능한 도서 목록을 조회한다.
     * 
     * @return 대출 가능한 도서 목록
     */
    List<Book> findByAvailableTrue();

    /**
     * 재고가 부족한 도서 목록을 조회한다.
     * 
     * @param threshold 재고 임계값
     * @return 재고가 임계값 이하인 도서 목록
     */
    @Query("SELECT b FROM Book b WHERE b.stockQuantity <= :threshold")
    List<Book> findBooksWithLowStock(@Param("threshold") int threshold);

    /**
     * 출판사별 도서 수를 조회한다.
     * 
     * @param publisher 출판사명
     * @return 해당 출판사의 도서 수
     */
    long countByPublisher(String publisher);
} 