package com.wanted.servlet.h_cookie_session;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/cookie")
public class CookieServlet extends HttpServlet {

    /* comment.
    *   Cookie 는 클라이언트(브라우저) 와 서버 간에 상태 정보를 유지하기 위해 사용되는
    *   작은 데이터 저장 공간이다.
    *   쿠키의 구조는 이름 과 값 쌍으로 구성되며, 선택적으로 만료시간, 경로, 등의 속성을
    *   부여할 수 있다. ex) name=홍길동 이 형태오 저장된다.
    *   쿠키의 특징
    *   - 클라이언트 측에서 저장 되므로 보안에 취약하다.
    *   - 크기에 제한이 있다. (4kb)
    * */


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Post 에 담겨있는 내용이 안 깨지게 하기 위한 Encoding 설정
        req.setCharacterEncoding("UTF-8");

        String cookieValue = req.getParameter("cookieValue");

        Cookie cookie = new Cookie("userValue", cookieValue);
        cookie.setMaxAge(60 * 60); // 1시간 만료 설정
        resp.addCookie(cookie);

        doGet(req, resp);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        String savedValue = "없음";

        Cookie[] cookies = req.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if("userValue".equals(cookie.getName())) {
                    savedValue = cookie.getValue();
                    break;
                }
            }
        }

        // Cookie 에 있는 값을 View 에서 보여주기 위해 넣어주기.
        req.setAttribute("savedValue", savedValue);

        // 지금까지 우리는 path 에 요청을 처리할 수 있는 서블릿 클래스만 넣었었다.
        RequestDispatcher rd = req.getRequestDispatcher("/h_cookie_session/cookie-result.jsp");
        rd.forward(req, resp);

    }
}
