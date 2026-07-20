package com.smartlearning.lms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 요청마다 request_id를 부여/이어받아 MDC에 넣는 필터.
 * FastAPI의 request_id 미들웨어(app/main.py)와 완전히 대칭이다.
 *
 * 두 가지 진입 경로를 모두 처리한다:
 *   1. Postman → Spring (/chat): 헤더가 없으므로 '새로 발급'
 *      → 이후 AiClientService가 FastAPI로 이 ID를 전파한다
 *   2. FastAPI → Spring (/api/students/...): FastAPI가 보낸
 *      X-Request-ID 헤더를 '이어받는다' ← 역호출 로그가 이어지는 지점!
 *
 * MDC(Mapped Diagnostic Context)란?
 *   로그 문맥을 담는 스레드별 저장소. 여기 넣어둔 값은 logback 패턴의
 *   %X{requestId}로 모든 로그에 자동으로 찍힌다. 코드 곳곳에서
 *   log.info("... " + rid) 하고 다닐 필요가 없다.
 *   (FastAPI 쪽의 ContextVar + logging.Filter 조합과 같은 역할)
 */
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-ID";   // FastAPI와 철자까지 동일!
    public static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String incoming = request.getHeader(HEADER);
        String rid = (incoming != null && !incoming.isBlank())
                ? incoming                                        // 이어받기
                : UUID.randomUUID().toString().substring(0, 8);   // 새로 발급

        MDC.put(MDC_KEY, rid);
        response.setHeader(HEADER, rid);   // Postman에서 rid 확인용

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 톰캣은 스레드를 '재사용'한다. 지우지 않으면 다음 요청의
            // 로그에 이전 요청의 rid가 찍히는 괴담이 벌어진다. 반드시 정리!
            MDC.remove(MDC_KEY);
        }
    }
}
