package com.wanted.servlet.g_redirect;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/redirect")
public class RedirectServlet extends HttpServlet {

    /* comment.
    *   redirect 는 사용자의 url 을 바꾼다.
    *   즉 url 변경은 마치 a 링크를 누른 것과 같은 효과이며
    *   get 방식이라고 볼 수 있다.
    * */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder responseText = new StringBuilder();
        responseText.append("<!doctype html>\n")
                .append("<html>\n")
                .append("<head>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<h3 align=\"center\">")
                .append("리다이렉트 성공!!!!!!!.</h3>")
                .append("</body>\n")
                .append("</html>\n");

        resp.setContentType("text/html; charset=UTF-8");

        PrintWriter out = resp.getWriter();

        out.print(responseText.toString());

        out.flush();
        out.close();

        /* comment.
        *   redirect 하면 url 이 재정의가 되며, 새로고침 시 redirect 된 페이지에 대한
        *   요청만 반복된다.
        *   즉, 이전 요청에 포함 된 정보는 남아있지 않고, url 이 변겨오디는 것이 redirect 의 특징이다.
        *   만약 redirect 된 서블릿이 이전 요청에 포함된 정보를 모른다면, 이전 요청 시에
        *   사용한 정보를 어떻게 전달할 수 있을까?
        *   -> redirect 시에도 정보를 유지하기 위해 Session 과 Cookie 가 등장한다.
        * */
    }
}
