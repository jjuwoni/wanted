package com.smartlearning.lms.ai;

import com.smartlearning.lms.dto.AiDtos.RecommendApiResponse;
import com.smartlearning.lms.dto.AiDtos.RecommendWebRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * [Day 4 신규] 강의 추천 엔드포인트 (Postman → Spring → FastAPI).
 *
 * ChatController와 같은 원칙:
 *   - student_id는 클라이언트를 믿지 않고 '서버가' 채운다
 *     (v1.0 결정: 하드코딩 상수 — ChatController.DEMO_STUDENT_ID 주석 참고)
 *   - 실패 처리는 이 컨트롤러에 없다! AiClientService가 던진
 *     AiServerException을 GlobalExceptionHandler가 계약 형식으로 변환한다.
 *     → FastAPI의 404/502가 detail 그대로 Postman까지 전달되는 것을
 *       [시나리오 E]에서 눈으로 확인해 보라.
 */
@RestController
@RequiredArgsConstructor
public class RecommendController {

    private static final Long DEMO_STUDENT_ID = 1L;   // 인증 도입 시 교체 지점

    private final AiClientService aiClientService;

    @PostMapping("/recommend")
    public RecommendApiResponse recommend(@RequestBody(required = false) RecommendWebRequest request) {
        int topK = (request != null && request.topK() != null) ? request.topK() : 3;
        return aiClientService.recommend(DEMO_STUDENT_ID, topK);
    }
}
