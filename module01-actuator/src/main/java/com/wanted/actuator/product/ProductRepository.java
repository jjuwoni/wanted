package com.wanted.actuator.product;

import com.wanted.actuator.product.dto.PopularProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            select new com.wanted.actuator.product.dto.PopularProductResponse(
                p.id, p.name, p.price, count(oi.id)
            )
            from OrderItem oi
            join oi.product p
            join oi.order o
            where o.createdAt >= :startDate
            group by p.id, p.name, p.price
            order by count(oi.id) desc, p.id asc
            """)
    List<PopularProductResponse> findPopularProducts(
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );
}
