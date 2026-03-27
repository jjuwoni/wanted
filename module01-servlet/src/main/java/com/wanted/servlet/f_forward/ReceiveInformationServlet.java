package com.wanted.servlet.f_forward;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/forward")
public class ReceiveInformationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String userId = req.getParameter("userId");
        String pwd = req.getParameter("password");

        System.out.println("userId = " + userId);
        System.out.println("pwd = " + pwd);

        // service.login(userId, pwd);
        System.out.println("로그인 완료!!!");

        /* comment.
        *   사용자가 ID, PWD 를 입력 후 로그인 버튼을 누르면
        *   /forward 요청을 처리하는 서블릿이 동작하는 것을 보았다.
        *   해당 서블릿은 사용자의 요청이 들어오면 비즈니스 로직을 처리하는 용도의 책임만 갖게 만들고자 한다.
        *   즉, 응답은 응답을 잘 해주는 친구에게 위임을 하겠다.
        * */

        // req 객체에 값을 넣는 메서드
        req.setAttribute("userId", userId);

        // 해당 서블릿에서 응답 특화 서블릿에게 요청을 보낼 것이다.
        // forward 는 다른 서블릿이 응답을 할 수 있게 보내는 역할을 한다.
        // 최초 요청 시 사용한 값을 응답 서블릿에서도 사용하기 위해서는 forward 시 req 객체와 resp 객체를 전달해주어야 한다.
        RequestDispatcher rd = req.getRequestDispatcher("/print");
        rd.forward(req, resp);

        /* comment. 중요!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        *   forward 는 서로 다른 서블릿과 req, resp 를 공유한다.
        *   따라서 요청을 위임 받는 서블릿에서도 정보를 공유할 수 있게 된다.
        *   forward 받은 서블릿의 존재를 클라이언트가 알 필요가 없기 때문에
        *   url 자체는 변경되지 않는다.
        *   forward 방식의 특징은 요청 시 서버로 전송한 데이터가 남아있는 상태로
        *   새로고침(재요청)을 하게 되면 동일한 요청을 반복해서 DB 에 Insert 하는 등의 행위가 발생할 수 있다.
        *   따라서 조회 시에는 forward 방식으로 하지만 Insert 등의 DML 구문은 forward 방식으로 처리하며 큰일난다.
        * */



    }
}
