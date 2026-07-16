package com.smartlearning.lms.ai;

import com.smartlearning.lms.dto.AiDtos.ChatApiRequest;
import com.smartlearning.lms.dto.AiDtos.ChatApiResponse;
import com.smartlearning.lms.dto.AiDtos.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * FastAPI(AI 서버)의 /chat 을 호출하는 서비스. (API 계약서 3장 — 구간 B)
 *
 * RestClientConfig에서 만든 aiRestClient 빈을 주입받아 쓴다.
 * (타임아웃/베이스URL/snake_case 직렬화가 전부 거기서 설정되어 있다 —
 *  이 클래스는 '호출과 에러 처리'에만 집중한다. 관심사 분리!)
 *
 * ─────────────────────────────────────────────────────────────────
 * ★ RestClient 사용 시 신경써야 할 것들 (설정편은 RestClientConfig 참고)
 * ─────────────────────────────────────────────────────────────────
 * 1. 에러의 '두 종류'를 구분해서 잡아라
 *    - 4xx/5xx 응답이 '온' 경우: 서버는 살아있고 거절/실패를 알려준 것
 *      → onStatus 또는 RestClientResponseException으로 처리
 *    - 응답이 '아예 안 온' 경우: 연결 거부, 타임아웃 (서버 다운!)
 *      → ResourceAccessException으로 처리
 *    이 둘은 사용자 안내 문구도, 로그 심각도도 달라야 한다.
 *
 * 2. AI 서버 장애가 LMS 전체 장애가 되게 하지 마라
 *    챗봇이 죽어도 수강신청은 되어야 한다. 그래서 예외를 위로 던지지
 *    않고, 이 계층에서 '정중한 실패 답변'으로 변환해서 반환한다.
 *    (FastAPI 쪽에서 Spring 장애를 {"error": ...}로 감싸던 것과 대칭!
 *     — 02_fc_spring.py의 _spring_get() 주석 참고)
 *
 * 3. 요청 본문 로깅에 주의
 *    question에는 개인적인 내용이 들어올 수 있다. 운영에서는
 *    본문 전체를 INFO 로그로 남기지 않는 것이 원칙이다.
 */
@Service
public class AiClientService {

    private final RestClient aiRestClient;

    // 생성자 주입: RestClientConfig의 @Bean aiRestClient가 들어온다
    public AiClientService(RestClient aiRestClient) {
        this.aiRestClient = aiRestClient;
    }

    /**
     * 챗봇 질문을 FastAPI로 전달하고 답변을 받아온다.
     *
     * @param question  사용자 질문
     * @param studentId 로그인 학생 ID (v1.0: ChatController의 하드코딩 상수)
     * @param history   과거 대화 (Spring이 보관 — FastAPI는 무상태)
     */
    public ChatApiResponse chat(String question, Long studentId, List<Message> history) {
        ChatApiRequest request = new ChatApiRequest(question, studentId, history);

        try {
            return aiRestClient.post()
                    .uri("/chat")                 // baseUrl(:8000)은 Config에서 설정됨
                    .body(request)                // Jackson이 snake_case JSON으로 직렬화
                    .retrieve()
                    // FastAPI가 4xx/5xx를 주면 기본적으로 예외가 터진다.
                    // onStatus로 '응답은 받았지만 실패'인 경우를 먼저 가로챈다.
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (req, res) -> {
                                // FastAPI 에러 형식 {"detail": ...} — 계약서 2장.
                                // 본문을 읽어 로그에 남기고 싶다면 res.getBody() 사용.
                                throw new IllegalStateException(
                                        "AI 서버 오류 응답: HTTP " + res.getStatusCode());
                            })
                    .body(ChatApiResponse.class); // DTO의 @JsonNaming에 따라 snake_case 역직렬화

        } catch (ResourceAccessException e) {
            // 연결 거부/타임아웃 = FastAPI가 죽었거나 60초를 넘겼다 (Config 참고).
            // 챗봇만 정중히 실패시키고 LMS는 계속 동작하게 한다. (원칙 2)
            return new ChatApiResponse(
                    "AI 도우미가 잠시 응답하지 못하고 있어요. 잠시 후 다시 시도해 주세요.",
                    List.of());

        } catch (IllegalStateException e) {
            return new ChatApiResponse(
                    "AI 도우미 처리 중 문제가 발생했어요. 잠시 후 다시 시도해 주세요.",
                    List.of());
        }
    }
}
