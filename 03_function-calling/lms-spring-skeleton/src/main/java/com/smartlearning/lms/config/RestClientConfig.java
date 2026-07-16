package com.smartlearning.lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * RestClient 설정 — Spring이 FastAPI(AI 서버)를 호출할 때 쓰는 HTTP 클라이언트.
 *
 * RestClient는 Spring 6.1 / Boot 3.2+의 동기 HTTP 클라이언트다.
 *   - RestTemplate의 후계자 (더 이상 RestTemplate으로 새 코드를 짜지 말 것)
 *   - WebClient의 fluent API 스타일이지만 리액티브 의존성 없이 동작
 *
 * ─────────────────────────────────────────────────────────────────
 * ★ 신경써야 할 것 1: 타임아웃 — '반드시' 명시적으로 설정하라
 * ─────────────────────────────────────────────────────────────────
 * 기본 팩토리의 타임아웃은 사실상 무제한에 가깝다. FastAPI가 장애로
 * 응답을 못 주면, 그 요청을 처리하던 톰캣 스레드가 '무기한' 붙잡힌다.
 * 이런 요청이 쌓이면? 톰캣 스레드 풀(기본 200개)이 고갈되어
 * AI와 무관한 회원가입/조회 API까지 전부 멈춘다.
 * → 외부 호출 하나가 서비스 전체를 죽이는 전형적인 장애 패턴!
 *
 * 값을 정하는 기준 (아무 숫자나 넣는 게 아니다):
 *   - connect: TCP 연결 수립까지의 제한. 서버가 죽었는지 판단은 빨라야
 *     하므로 짧게(3초). 연결 자체가 안 되면 오래 기다릴 이유가 없다.
 *   - read: 연결 후 응답 도착까지의 제한. ★ LLM은 느리다! ★
 *     Gemini가 도구를 2~3번 호출하며 답을 만들면 10~30초도 걸린다.
 *     일반 API 감각으로 5초를 줬다간 정상 응답도 타임아웃으로 끊긴다.
 *     → 60초로 넉넉히. (API 계약서 2장에 합의된 값)
 *
 * ─────────────────────────────────────────────────────────────────
 * ★ 신경써야 할 것 2: JSON 필드 이름 — snake_case 계약
 * ─────────────────────────────────────────────────────────────────
 * FastAPI(Pydantic)는 snake_case(student_id), Java는 camelCase(studentId).
 * 아무 설정이 없으면 Spring이 보낸 studentId를 FastAPI가 못 알아본다
 * (422 에러). 전역 ObjectMapper를 바꾸면 브라우저 API까지 snake_case가
 * 되므로, FastAPI 전용 DTO에 @JsonNaming(SnakeCaseStrategy.class)을 붙여
 * 통신 경계를 분리했다. RestClient는 해당 DTO의 명명 전략에 따라
 * 요청을 직렬화하고 응답을 역직렬화한다.
 *
 * ─────────────────────────────────────────────────────────────────
 * ★ 신경써야 할 것 3: CORS는 여기서 걱정할 문제가 아니다
 * ─────────────────────────────────────────────────────────────────
 * RestClient는 서버에서 실행되는 코드다. CORS는 브라우저 정책이므로
 * 이 호출(Spring→FastAPI)에는 애초에 적용되지 않는다.
 * 자세한 설명은 CorsConfig.java 상단 주석 참고.
 *
 * ─────────────────────────────────────────────────────────────────
 * ★ 신경써야 할 것 4: 재시도(retry)는 함부로 넣지 마라
 * ─────────────────────────────────────────────────────────────────
 * 일반 조회 API라면 실패 시 재시도가 미덕이지만, LLM 호출은
 *   1) 호출 1번 = 비용이고  2) 타임아웃 직전까지 갔다가 재시도하면
 * 사용자는 2분을 기다리게 된다. AI 호출 실패는 재시도보다
 * "즉시 정중한 실패 응답"이 나은 경우가 많다 (AiClientService 참고).
 */
@Configuration
public class RestClientConfig {

    // 주소를 코드에 하드코딩하지 않고 application.yml에서 주입받는다.
    // → 배포 환경(개발/운영)마다 yml만 바꾸면 된다.
    @Value("${ai.server.base-url}")
    private String aiServerBaseUrl;

    @Bean
    public RestClient aiRestClient(RestClient.Builder builder) {
        // 타임아웃은 RestClient가 아니라 그 아래의 '요청 팩토리' 설정이다
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(3))   // 연결: 짧게
                .withReadTimeout(Duration.ofSeconds(60));    // 응답: LLM이라 길게!

        ClientHttpRequestFactory factory = ClientHttpRequestFactories.get(settings);

        return builder                       // ← Boot 자동구성 builder
                .baseUrl(aiServerBaseUrl)    // 이후 .uri("/chat")처럼 경로만 쓰면 된다
                .requestFactory(factory)
                .build();
    }
}
