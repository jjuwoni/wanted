package com.wanted.servlet.i_filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/* comment.
*   XML 방식이 아닌 annotation 방식으로 filter 등록
* */

// 모든 요청에 동작하게 만드는 LoggingServlet
@WebFilter("/*")
public class LoggingServlet implements Filter {

    /* comment.
    *   해당 필터는 Http Request 가 들어오면 시간을 측정한다.
    *   또한, Http Response 를 하기 전 시간을 종료하여
    *   최종 요청/응답 수행 시간을 체크한다.
    * */

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        long startTime = System.currentTimeMillis();
        System.out.println("요청 시작!!! " + req.getRequestURI());

        // 다음 서블릿 혹은 필터 호출 시작
        chain.doFilter(request, response);

        long endTime = System.currentTimeMillis();
        System.out.println("요청 종료 : " + req.getRequestURI() + " 의 소요 시간 = " + (endTime - startTime) + "(ms)");
    }

    @Override
    public void destroy() {

    }
}
