package com.smartlearning.lms.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

/**
 * FastAPI /chat 과 주고받는 DTO — API 계약서 3장과 필드가 1:1로 같아야 한다!
 *
 * FastAPI 쪽 Pydantic 모델(03_router_chat_api.py)과의 대응:
 *   ChatApiRequest  ↔ ChatRequest(question, student_id, history)
 *   ChatApiResponse ↔ ChatResponse(answer, used_tools)
 * FastAPI 전용 DTO에만 SnakeCaseStrategy를 적용하여 브라우저 API의
 * 기본 camelCase 계약과 분리한다.
 */
public class AiDtos {

    public record Message(String role, String content) {}

    /** Spring → FastAPI 요청 본문 */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ChatApiRequest(String question, Long studentId, List<Message> history) {}

    /** FastAPI → Spring 응답 본문 */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ChatApiResponse(String answer, List<String> usedTools) {}

    /** 브라우저 → Spring 요청 본문 (studentId 없음 — 서버가 채운다!) */
    public record ChatWebRequest(String question, List<Message> history) {}

    /** Spring → 브라우저 응답 본문 (기본 camelCase 계약) */
    public record ChatWebResponse(String answer, List<String> usedTools) {}
}
