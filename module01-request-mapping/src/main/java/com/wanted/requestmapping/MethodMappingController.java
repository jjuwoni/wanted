package com.wanted.requestmapping;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/* comment.
*   Spring Boot App 은 Tomcat 이 내장되어 있다.
*   지금까지는 요청을 처리할 Servlet 을 만들었다면,
*   이제 Servlet 은 Http 요청을 받는 즉시 @Controller
*   어노테이션이 달린 클래스에 처리를 위임한다.
* */
@Controller
public class MethodMappingController {

    /* comment.
    *   1. 메소드 방식 미지정
    *   - GET, POST 방식 상관 없이 사용자의 요청을 모두 처리한다.
    *   - url 일치하는 모든 방식의 요청
    * */
    @RequestMapping("/menu/regist")
    public String registMenu(Model model) {

        /* model 객체란?
        *   model 객체는 server 에서 발생한 값을 담을 수 있는
        *   객체이다. key-value 형태로 값을 담게 되며,
        *   응답 시 view 에서 Model 에 담긴 값을 사용할 수 있게된다.
        *   addAttribute() 메소드로 값을 넣을 수 있다.
        * */

        // service.methodA();

        model.addAttribute("message", "메뉴 등록용 핸들러 메소드 동작 확인..");

        /* comment.
        *   Controller 계층의 마지막 임무는 사용자에게
        *   어떤 응답을 해줄 것인지 결정한다.
        *   메소드의 반환타입을 String 으로 한 후, 문자열을 return 하면
        *   resources/templates 하위 경로의 파일을 탐색한다.
        * */
        return "mappingResult";
    }

    /* comment.
    *   2. 메소드 방식 지정
    * */
    @RequestMapping(value = "/menu/regist", method = RequestMethod.GET)
    public String getRegistMenu(Model model) {

        model.addAttribute("message", "메뉴 등록용 GET 방식의 핸들러 메소드 동작 확인..");

        return "mappingResult";
    }

    @RequestMapping(value = "/menu/regist", method = RequestMethod.POST)
    public String postRegistMenu(Model model) {

        model.addAttribute("message", "메뉴 등록용 POST 방식의 핸들러 메소드 동작 확인..");

        return "mappingResult";
    }

    @GetMapping("/menu/modify")
    public String getModifyMenu(Model model) {

        model.addAttribute("message", "메뉴 수정용 GET 방식의 핸들러 메소드 동작 확인..");

        return "mappingResult";
    }

    @PostMapping("/menu/modify")
    public String postModifyMenu(Model model) {

        model.addAttribute("message", "메뉴 수정용 POST 방식의 핸들러 메소드 동작 확인..");

        return "mappingResult";
    }


}
