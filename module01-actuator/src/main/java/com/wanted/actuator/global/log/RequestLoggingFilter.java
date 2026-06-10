package com.wanted.actuator.global.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/* comment.
*   Metric 과 Log 를 어떤 식으로 분류해서 사용을 할까?
*   - 메트릭으로 이상을 발견하고, 로그로 원인을 좁혀간다.
*   메트릭은 "오류가 볓 번 발생했는가?", "응답 시간이 얼마나 늘었는가?"
*   등에 대한 집계 된 숫자에 강하다.
*   하지만 사용자의 요청 하나가 어떤 비즈니스 로직을 지나가며 왜 실패했는 지는
*   메트릭으로는 한계가 있다.
*   이러한 한계는 Log 로 극복한다.
* */

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    public static final String REQUEST_ID_HEADER = "X-Request_Id";
    public static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String traceId = resolveTraceId(request);
        long startAt = System.nanoTime();

        MDC.put(TRACE_ID_MDC_KEY, traceId);
        response.setHeader(REQUEST_ID_HEADER, traceId);

        try {

            log.info("event=request_started method={} uri={}", request.getMethod(), request.getRequestURI());
            // 다음 필터 동작, 없으면 Controller
            filterChain.doFilter(request, response);
        } finally {

            // 소요 시간
            long durationMs = (System.nanoTime() - startAt) / 1_000_000;
            log.info(
                    "event=request_completed method={} uri={} status={} durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs
            );

            MDC.remove(TRACE_ID_MDC_KEY);

        }

    }

    private String resolveTraceId(HttpServletRequest request) {
        // 사용자 요청 헤더에서 requestID 꺼내기
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if(requestId == null || requestId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        // 요청 별 식별 id 반환
        return requestId;
    }
}
