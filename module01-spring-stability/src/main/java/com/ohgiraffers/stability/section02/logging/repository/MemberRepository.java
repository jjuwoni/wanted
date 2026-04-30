package com.ohgiraffers.stability.section02.logging.repository;

import com.ohgiraffers.stability.section02.logging.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 회원 데이터 접근을 위한 Repository 인터페이스
 * 
 * <p>회원 엔티티에 대한 CRUD 작업과 커스텀 쿼리를 제공한다.
 * JpaRepository를 상속받아 기본적인 데이터베이스 작업을 지원한다.</p>
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일로 회원을 조회한다.
     * 
     * @param email 조회할 이메일
     * @return 조회된 회원 정보 (Optional)
     */
    Optional<Member> findByEmail(String email);

    /**
     * 이메일 존재 여부를 확인한다.
     * 
     * @param email 확인할 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);

    /**
     * 활성 상태인 회원 목록을 조회한다.
     * 
     * @return 활성 회원 목록
     */
    List<Member> findByActiveTrue();

    /**
     * 회원명에 특정 키워드가 포함된 회원 목록을 조회한다.
     * 
     * @param keyword 검색 키워드
     * @return 검색된 회원 목록
     */
    List<Member> findByMemberNameContainingIgnoreCase(String keyword);

    /**
     * 활성 상태별 회원 수를 조회한다.
     * 
     * @param active 활성 상태
     * @return 해당 상태의 회원 수
     */
    long countByActive(boolean active);
} 