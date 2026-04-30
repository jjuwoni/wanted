package com.wanted.springtest.section02.jpa;

/* comment.
*   Repository 계층 테스트
*   @DataJpaTest
*   - 핵심 기능만 간소화 하여 Spring Data Jpa 의 동작을 테스트한다.
* */

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CalculationHistoryRepositoryTest {

    // @DataJpaTest 가 자동으로 Repository 를 Bean 으로 등록한다.
    @Autowired
    private CalculationHistoryRepository calculationHistoryRepository;

    @Test
    void 계산기록_저장_및_조회_테스트() {

        // given 계산 기록 생성
        CalculationHistory history = new CalculationHistory("ADD", 10.0, 5.0, 15.0);

        // when 계산 기록 저장 후 조회
        CalculationHistory savedHistory = calculationHistoryRepository.save(history);
        CalculationHistory foundHistory = calculationHistoryRepository.findById(savedHistory.getId()).orElse(null);

        // then
        assertNotNull(foundHistory);

    }
}
