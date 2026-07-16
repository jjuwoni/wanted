package com.smartlearning.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 설정 — 시작 전에 가장 흔한 오해부터 바로잡자.
 *
 * ★★★ CORS는 '브라우저'에만 적용되는 보안 정책이다 ★★★
 *
 * 우리 시스템의 세 가지 호출 구간 (API 계약서 1장 그림 참고):
 *   (A) 브라우저 → Spring        : CORS 적용 대상 ← "이 클래스가 필요한 이유"
 *   (B) Spring   → FastAPI (RestClient) : CORS와 무관!
 *   (C) FastAPI  → Spring  (httpx)      : CORS와 무관!
 *
 * 왜 (B), (C)는 무관한가?
 *   CORS(Cross-Origin Resource Sharing)는 "악성 사이트의 JS가 사용자의
 *   브라우저를 이용해 다른 사이트의 API를 몰래 호출"하는 것을 막기 위해
 *   '브라우저가' 스스로 지키는 규칙이다. 서버 프로세스(RestClient, httpx,
 *   curl, Postman)는 브라우저가 아니므로 이 규칙 자체가 없다.
 *   → "RestClient 호출이 CORS 때문에 막혔어요"라는 진단은 100% 오진이다.
 *     (그 에러는 방화벽, 포트, 오타 등 다른 원인이다)
 *
 * 그럼 언제 CORS 에러를 만나는가?
 *   Day 4에 챗봇 화면(HTML/JS)을 만들 때다. 예를 들어 화면을
 *   VSCode Live Server(:5500)로 띄우고 Spring(:8080)을 fetch로 호출하면
 *   출처(origin = 프로토콜+호스트+포트)가 달라서 브라우저가 차단한다.
 *   콘솔에 뜨는 그 유명한 빨간 에러:
 *   "blocked by CORS policy: No 'Access-Control-Allow-Origin' header..."
 *
 * 동작 원리 요약:
 *   1. 브라우저가 본 요청 전에 예비 요청(preflight, OPTIONS 메서드)을 보내
 *      "이 origin에서 POST 해도 돼?"라고 서버에 물어본다.
 *   2. 서버가 Access-Control-Allow-Origin 등의 응답 헤더로 허락하면
 *      그제서야 본 요청을 보낸다. 아래 설정이 바로 그 '허락 응답'을 만든다.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                       // 모든 경로에 적용
                // 허용할 출처를 '구체적으로' 나열한다.
                // 개발 편의로 "*"(전부 허용)을 쓰고 싶어지지만,
                //   1) allowCredentials(true)와 "*"는 함께 쓸 수 없고 (스펙 금지)
                //   2) 운영 배포 시 보안 사고의 지름길이다.
                // 필요한 출처만 명시하는 습관을 들이자.
                .allowedOrigins(
                        "http://localhost:3000"           // React 개발 서버를 쓰는 팀용
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                // 쿠키/인증정보를 함께 보낼지 여부.
                // 지금은 인증이 없지만(학생 ID 하드코딩) 추후 세션 로그인을
                // 붙이면 true가 필요하므로 미리 켜둔다.
                .allowCredentials(true)
                // preflight(OPTIONS) 결과를 브라우저가 캐시하는 시간(초).
                // 매 요청마다 예비 요청이 나가는 낭비를 줄여준다.
                .maxAge(3600);
    }

    /*
     * [참고] FastAPI 쪽에는 CORS 설정을 안 해도 되나?
     *   우리 아키텍처에서 브라우저는 'Spring만' 호출하고,
     *   FastAPI는 Spring이 서버 대 서버로 호출한다(구간 B). → 불필요.
     *   만약 브라우저가 FastAPI(:8000)를 직접 fetch하는 구조로 바꾼다면
     *   그때는 FastAPI에 CORSMiddleware를 추가해야 한다:
     *     from fastapi.middleware.cors import CORSMiddleware
     *     app.add_middleware(CORSMiddleware, allow_origins=[...], ...)
     */
}
