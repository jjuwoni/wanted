package com.wanted.servlet.h_cookie_session;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/expire")
public class SessionExpireServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");

        // false 로 하게 되면, session 을 새로 만들지 않고
        // 기존 세션만 조회하게 된다.
        HttpSession session = req.getSession(false);

        // 세션이 존재하면
        if(session != null) {
            // 세션을 무효화 하는 메서드
            session.invalidate();
        }
        // 세션 무효화 후 /session 페이지로 리다이렉트
        resp.sendRedirect("/session");
    }
}
