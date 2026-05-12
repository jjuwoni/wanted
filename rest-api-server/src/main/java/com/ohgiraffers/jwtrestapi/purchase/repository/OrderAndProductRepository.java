package com.ohgiraffers.jwtrestapi.purchase.repository;

import com.ohgiraffers.jwtrestapi.purchase.entity.OrderAndProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderAndProductRepository extends JpaRepository<OrderAndProduct , Integer> {

    // 회원 별 주문 목록 조회
    List<OrderAndProduct> findByOrderMember(int memberCode);
}
