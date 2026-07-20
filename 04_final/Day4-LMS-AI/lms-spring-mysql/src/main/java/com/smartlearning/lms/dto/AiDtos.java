package com.smartlearning.lms.dto;

import java.util.List;

/**
 * [Day 4 교체본] FastAPI와 주고받는 DTO — 추천 관련 record 추가.
 *
 * FastAPI 쪽 Pydantic 모델과의 대응 (계약서 3장):
 *   ChatApiRequest       ↔ app/schemas/chat.py  ChatRequest
 *   ChatApiResponse      ↔ app/schemas/chat.py  ChatResponse
 *   RecommendApiRequest  ↔ app/schemas/recommend.py RecommendRequest
 *   RecommendApiResponse ↔ app/schemas/recommend.py RecommendResponse
 * (camelCase ↔ snake_case 변환은 Jackson SNAKE_CASE 전략이 담당)
 */
public class AiDtos {

    public record Message(String role, String content) {}

    // --- 챗봇 ---
    public record ChatApiRequest(String question, Long studentId, List<Message> history) {}

    public record ChatApiResponse(String answer, List<String> usedTools) {}

    /** 브라우저/Postman → Spring 요청 본문 (student_id 없음 — 서버가 채운다!) */
    public record ChatWebRequest(String question, List<Message> history) {}

    // --- 추천 (Day 4 추가) ---
    public record RecommendApiRequest(Long studentId, int topK) {}

    public record RecommendedCourse(String code, String title, String category,
                                    String level, double similarity) {}

    public record RecommendApiResponse(String basedOn,
                                       List<RecommendedCourse> recommendations,
                                       String explanation) {}

    /** Postman → Spring 추천 요청 (student_id 없음 — 서버가 채운다) */
    public record RecommendWebRequest(Integer topK) {}
}
