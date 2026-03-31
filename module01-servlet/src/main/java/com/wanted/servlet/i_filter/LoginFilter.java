package com.wanted.servlet.i_filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/auth/*")
public class LoginFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Login filter 초기화 됨...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        /* comment.
        *   /auth 하위 요청을 filter 하며, 해당 필터는
        *   session 에 loggedInUser 값이 없으면, 500 Error 를 발생시키는 것이 아닌,
        *   로그인을 할 수 있는 페이지로 redirect 시킬 것이다.
        * */

        // 기존 세션을 가져옴.
        HttpSession session = req.getSession(false);
        if(session == null || session.getAttribute("loggedInUser") == null) {
            resp.sendRedirect("/h_cookie_session/cookie_session.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        System.out.println("login filter 파괴됨...");
    }
}
