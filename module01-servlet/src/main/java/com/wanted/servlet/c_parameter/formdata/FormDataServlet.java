package com.wanted.servlet.c_parameter.formdata;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/formdata")
public class FormDataServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /* comment.
        *   GET 방식과 POST 방식의 가장 큰 차이는
        *   URL 로 데이터를 전달하는 지에 대한 여부이다.
        *   GET 방식의 요청은 URL 에 데이터가 노출이 되며,
        *   POST 방식의 요청은 URL이 아닌, Body 영역에 데이터가 담겨 전달 되는 방식이다.
        *   GET 방식은 URL에 데이터가 전달 되어 보안 상 취약하다.
        *   또한, URL 에 전달할 수 있는 데이터의 크기도 한정되어 있다.
        *   다만 속도가 빠르기 때문에 조회 시에 GET 방식을 사용한다.
        *   POST 방식은 URL에 데이터가 노출되지 않으며,
        *   사용자가 입력하는 데이터 등을 payload (body) 영역에 담아서 전달한다.
        *   전달할 수 있는 데이터의 크기는 GET 방식보다 훨씬 크다.
        *   다만 속도가 느리기 때문에 단순 조회 시에는 POST 대신 GET 을 사용한다.
        * */

        String name = req.getParameter("name");
        System.out.println("name = " + name);

        int age = Integer.parseInt(req.getParameter("age"));
        System.out.println("age = " + age);
    }
}
