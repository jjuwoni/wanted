package com.wanted.restapi.section01.response;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

//@Controller
@RestController
public class ResponseController {

    /* comment.
    *   @Controller : View 를 반환해야 하는 책임이 있다.
    *   @RestController : View 는 반환하지 않으며 데이터만 반환한다.
    *   - @Controller + @ResponseBody 가 합쳐진 개념이다.
    *   - @ResponseBody
    *   - 메서드레벨, 클래스레벨에 작성할 수 있으며, 해당 메서드의 반환값은
    *   - 응답 본문(Body) 영역에 직접 쓰여지며, ViewResolver 를 거치지 않는다.
    * */

//    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "안녕 세상!";
    }

    // 객체를 return
    /* comment.
    *   JSON : 자바스크립트 객체 표기법
    *   - 자바스크립트는 웹상에서 동작하는 언어이다.
    *   - 따라서 웹 개발자의 경우 99% 이상 어떤 프레임워크를 사용하던
    *   - 어떤 언어를 사용하던 JSON 형태로 데이터를 return 한다.
    *   JSON 표현식
    *   {
    *       key : value
    *   }
    *   - Spring 에서 우리가 만든 객체를 return 하게 되면
    *   - 알아서 JSON 형태로 응답해준다.
    *   - Jackson 라이브러리가 Spring 기본 내장되어 있으며
    *   - 해당 라이브러리는 Java Class <-> JSON 간의 파싱을
    *   - 자동으로 처리해준다.
    * */
    @GetMapping("/object")
    public Message getObject() {
        return new Message(200, "객체를 리턴합니다!");
    }

    // 배열 return
    @GetMapping("/list")
    public List<String> getList() {

        List<String> stringList = new ArrayList<>();
        stringList.add("사과");
        stringList.add("배");
        stringList.add("딸기");

        return stringList;
    }

}
