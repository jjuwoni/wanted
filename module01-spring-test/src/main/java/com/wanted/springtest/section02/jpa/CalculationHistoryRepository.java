package com.wanted.springtest.section02.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculationHistoryRepository extends JpaRepository<CalculationHistory, Long> {
    List<CalculationHistory> findByOperation(String add);
}
