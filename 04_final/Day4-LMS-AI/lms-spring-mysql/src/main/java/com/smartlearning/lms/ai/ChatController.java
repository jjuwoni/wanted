package com.smartlearning.lms.ai;

import com.smartlearning.lms.dto.AiDtos.ChatApiResponse;
import com.smartlearning.lms.dto.AiDtos.ChatWebRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 브라우저(챗봇 화면)가 호출하는 엔드포인트. (API 계약서 1장 — 구간 A)
 *
 * 여기가 CORS의 무대다! 브라우저가 이 API를 fetch로 호출하므로
 * 화면의 origin(:5500 등)이 CorsConfig.allowedOrigins에 있어야 한다.
 * (반면 이 컨트롤러가 이어서 호출하는 FastAPI 구간은 CORS 무관 — 구간 B)
 *
 * 요청 흐름:
 *   브라우저 ──POST /chat──> 이 컨트롤러 ──AiClientService──> FastAPI ──> Gemini
 *                                                              │
 *                              StudentApiController <──역호출──┘ (Function Calling)
 */
@RestController
@RequiredArgsConstructor
public class ChatController {

    /**
     * ★ 로그인 학생 ID 하드코딩 (v1.0 결정 — API 계약서 1장) ★
     *
     * 인증/인가는 이번 과정의 범위 밖이므로 "누가 로그인했는가"를
     * 이 상수 하나로 고정한다. data.sql의 학생 1번(김민준)이 주인공.
     *
     * 왜 브라우저가 student_id를 보내게 하지 않는가?
     *   클라이언트가 보내는 ID를 믿으면, 아무나 남의 학번을 넣어
     *   남의 성적을 조회할 수 있다(치명적 보안 결함). 신원은 반드시
     *   '서버가' 결정해야 한다. 지금은 그 결정이 상수일 뿐이다.
     *
     * 추후 Spring Security 도입 시 교체 지점:
     *   이 상수를 지우고 아래처럼 인증 객체에서 꺼내면 끝이다.
     *   chat(..., @AuthenticationPrincipal LoginUser user) → user.getId()
     *   → 컨트롤러 바깥(AiClientService, FastAPI)은 한 줄도 안 바뀐다!
     */
    private static final Long DEMO_STUDENT_ID = 1L;

    private final AiClientService aiClientService;

    @PostMapping("/chat")
    public ChatApiResponse chat(@RequestBody ChatWebRequest request) {
        // 브라우저가 보낸 것: question, history
        // 서버가 채우는 것: studentId  ← 신원은 서버의 책임!
        List<com.smartlearning.lms.dto.AiDtos.Message> history =
                request.history() == null ? List.of() : request.history();

        return aiClientService.chat(request.question(), DEMO_STUDENT_ID, history);
    }

    /*
     * TODO(Day 4): 대화 이력(history)을 지금은 브라우저 JS가 배열로 들고
     *   있다가 매번 보냅니다. 이를 서버 세션이나 chat_message 테이블에
     *   저장하도록 바꾸면 새로고침해도 대화가 유지됩니다. (선택 과제)
     */
}
