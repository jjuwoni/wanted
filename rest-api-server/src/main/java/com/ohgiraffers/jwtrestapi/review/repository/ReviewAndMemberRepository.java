package com.ohgiraffers.jwtrestapi.review.repository;

import com.ohgiraffers.jwtrestapi.review.entity.ReviewAndMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewAndMemberRepository extends JpaRepository<ReviewAndMember , Integer> {

    // 제품 기준 리뷰를 페이징 처리까지 한 조회 메서드
    Page<ReviewAndMember> findByProductCode(Integer integer, Pageable pageable);
}
