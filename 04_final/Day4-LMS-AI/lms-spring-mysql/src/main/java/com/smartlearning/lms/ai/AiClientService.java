package com.smartlearning.lms.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlearning.lms.config.RequestIdFilter;
import com.smartlearning.lms.dto.AiDtos.ChatApiRequest;
import com.smartlearning.lms.dto.AiDtos.ChatApiResponse;
import com.smartlearning.lms.dto.AiDtos.Message;
import com.smartlearning.lms.dto.AiDtos.RecommendApiRequest;
import com.smartlearning.lms.dto.AiDtos.RecommendApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

/**
 * [Day 4 교체본] FastAPI 호출 서비스 — 로깅/예외 연동이 추가되었다.
 *
 * Day 3 버전과의 차이 3가지 (diff 포인트):
 *   1. ★ X-Request-ID 헤더 전파 — MDC의 rid를 FastAPI로 실어 보낸다.
 *      이 한 줄이 "Postman → Spring → FastAPI → (역호출) Spring" 전
 *      구간의 로그를 하나의 rid로 묶는다. (RequestIdFilter 주석 참고)
 *   2. 요청/응답/실패를 '로그로' 남긴다 — rid는 logback 패턴이
 *      자동으로 붙이므로 메시지에 rid를 넣을 필요가 없다.
 *   3. 에러 정책을 API 성격에 따라 이원화했다:
 *      - chat():      실패해도 '정중한 답변'으로 변환 (챗봇은 죽지 않는다)
 *      - recommend(): 실패를 AiServerException으로 '승격'
 *                     → GlobalExceptionHandler가 FastAPI의 상태코드와
 *                       detail을 그대로 이어 전달 (예외의 전 구간 연결!)
 *      왜 다르게? 챗봇의 실패는 '대화'로 수습 가능하지만, 추천 API는
 *      호출자가 성공/실패를 명확히 구분해야 하는 '데이터 API'이기 때문.
 */
@Service
public class AiClientService {

    private static final Logger log = LoggerFactory.getLogger(AiClientService.class);

    private final RestClient aiRestClient;
    private final ObjectMapper objectMapper;   // FastAPI 에러 본문 {"detail":...} 파싱용

    public AiClientService(RestClient aiRestClient, ObjectMapper objectMapper) {
        this.aiRestClient = aiRestClient;
        this.objectMapper = objectMapper;
    }

    /** 현재 요청의 rid — RequestIdFilter가 MDC에 넣어둔 값 */
    private String currentRequestId() {
        String rid = MDC.get(RequestIdFilter.MDC_KEY);
        return rid != null ? rid : "-";
    }

    /** FastAPI 에러 본문에서 detail을 꺼낸다 (실패하면 원문 일부 반환) */
    private String extractDetail(String responseBody) {
        try {
            JsonNode node = objectMapper.readTree(responseBody);
            return node.has("detail") ? node.get("detail").asText() : responseBody;
        } catch (Exception e) {
            return responseBody.length() > 200 ? responseBody.substring(0, 200) : responseBody;
        }
    }

    // ------------------------------------------------------------------
    // 기능 ①: RAG 챗봇 — 실패 시 '정중한 답변'으로 변환 (장애 격리)
    // ------------------------------------------------------------------
    public ChatApiResponse chat(String question, Long studentId, List<Message> history) {
        log.info("AI 서버 호출 → POST /chat (student_id={})", studentId);
        try {
            ChatApiResponse response = aiRestClient.post()
                    .uri("/chat")
                    .header(RequestIdFilter.HEADER, currentRequestId())  // ★ rid 전파!
                    .body(new ChatApiRequest(question, studentId, history))
                    .retrieve()
                    .body(ChatApiResponse.class);
            log.info("AI 서버 응답 ← /chat used_tools={}", response.usedTools());
            return response;

        } catch (RestClientResponseException e) {
            // 4xx/5xx '응답이 온' 경우 — FastAPI 로그에서 같은 rid로 원인 확인 가능
            log.warn("AI 서버 오류 응답 {} body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return new ChatApiResponse(
                    "AI 도우미 처리 중 문제가 발생했어요. 잠시 후 다시 시도해 주세요.",
                    List.of());

        } catch (ResourceAccessException e) {
            // 연결 거부/타임아웃 — 응답 자체가 안 온 경우 (RestClientConfig 참고)
            log.error("AI 서버 연결 실패: {}", e.getMessage());
            return new ChatApiResponse(
                    "AI 도우미가 잠시 응답하지 못하고 있어요. 잠시 후 다시 시도해 주세요.",
                    List.of());
        }
    }

    // ------------------------------------------------------------------
    // 기능 ②: 강의 추천 — 실패를 예외로 '승격' (상태코드/메시지 그대로 전달)
    // ------------------------------------------------------------------
    public RecommendApiResponse recommend(Long studentId, int topK) {
        log.info("AI 서버 호출 → POST /courses/recommend (student_id={})", studentId);
        try {
            RecommendApiResponse response = aiRestClient.post()
                    .uri("/courses/recommend")
                    .header(RequestIdFilter.HEADER, currentRequestId())  // ★ rid 전파!
                    .body(new RecommendApiRequest(studentId, topK))
                    .retrieve()
                    .body(RecommendApiResponse.class);
            log.info("AI 서버 응답 ← /courses/recommend based_on={}", response.basedOn());
            return response;

        } catch (RestClientResponseException e) {
            // 예: FastAPI 404 "수강 이력이 없어 추천할 수 없습니다"
            //     → 그 404와 detail을 그대로 이어서 던진다 (핸들러가 응답 변환)
            String detail = extractDetail(e.getResponseBodyAsString());
            log.warn("AI 서버 오류 응답 {} detail={}", e.getStatusCode(), detail);
            throw new AiServerException(e.getStatusCode().value(), detail);

        } catch (ResourceAccessException e) {
            log.error("AI 서버 연결 실패: {}", e.getMessage());
            throw new AiServerException(503, "AI 추천 서버에 연결할 수 없습니다.");
        }
    }
}
