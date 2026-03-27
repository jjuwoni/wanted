package com.wanted.servlet.e_exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/showerrorpage")
public class ExceptionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("showerrorpage 호출됨..");

        String forwardUrl = req.getAttribute("jakarta.servlet.forward.request_uri").toString();
        System.out.println("forwardUrl = " + forwardUrl);

        int statusCode = (int) req.getAttribute("jakarta.servlet.error.status_code");
        String msg = req.getAttribute("jakarta.servlet.error.message").toString();

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("<!doctype html>\n")
                .append("<html>\n")
                .append("<head>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<h1>")
                .append(statusCode)
                .append(" - ")
                .append(msg)
                .append("</body>\n")
                .append("</html>\n");
        // 아래 구문은 응답 시에 항상 해줘야 한글이 안 깨진다.
        // 요청 or 응답 할 때마다 항상 해줘야 하는 건데
        // 더 쉽게 처리할 수 있는 방법이 있지 않을까?
        resp.setContentType("text/html; charset=UTF-8");

        PrintWriter out = resp.getWriter();
        out.print(responseBuilder);
        out.flush();
        out.close();

    }
}
