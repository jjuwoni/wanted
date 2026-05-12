package com.ohgiraffers.jwtrestapi.review.repository;

import com.ohgiraffers.jwtrestapi.review.entity.Review;
import com.ohgiraffers.jwtrestapi.review.entity.ReviewAndMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review , Integer> {

    // count 는 반환형 long
    long countByProductCode(int productCode);


}
