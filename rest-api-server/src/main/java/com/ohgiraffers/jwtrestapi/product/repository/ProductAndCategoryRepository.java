package com.ohgiraffers.jwtrestapi.product.repository;

import com.ohgiraffers.jwtrestapi.product.entity.ProductAndCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAndCategoryRepository extends JpaRepository<ProductAndCategory , Integer> {
}
