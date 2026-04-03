package com.wanted.exceptionhandler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/* comment.
*   Advice 는  AOP 파트에서,
*   반복적으로 동작해야 할 코드를 뭉쳐둔 메소드를
*   의미하는 단어였다.
*   @ControllerAdvice 마찬가지로 공통으로 발생하는
*   예외처리를 담당할 수 있는 기능을 수행한다.
* */

@ControllerAdvice
public class GlobalExceptionHandler {

    /* comment.
    *   @ExceptionHandler 의 우선순위
    *   예외가 발생한 클래스에 핸들러가 있으면, 클래스 레벨의 핸들러가 높은 우선순위를 갖는다.
    *   만약 예외가 발생한 클래스에 핸들러가 없으면, 차순위로 전역의 핸들러가 동작한다.
    * */

    @ExceptionHandler(NullPointerException.class)
    public String globalNull(Model model, NullPointerException e) {
        System.out.println("전역 레벨의 예외 처리!");
        model.addAttribute("key", e.getMessage());

        return "error/nullPointer";

    }

    @ExceptionHandler(MemberNotFoundException.class)
    public String globalMemberException(Model model, MemberNotFoundException e) {

        model.addAttribute("key", e);

        return "error/memberNotFound";
    }

    /* comment.
    *   예외처리 클래스의 부모인 Exception 클래스를 Handling
    *   예외처리 클래스는 모두 Exception 을 상속받아 만들어져있다.
    *   또한 우리가 개발 시 발생할 수 있는 모든 예외를 핸들링 할 수 없기 때문에
    *   대표적인 예외처리들만 핸들링, 사용자 정의의 예외처리들만 핸들링을 해두고
    *   그 외 예외는 Exception 으로 default 설정을 해둔다.
    * */
    @ExceptionHandler(Exception.class)
    public String defaultException(Model model, Exception e) {

        model.addAttribute("class", e.getClass());
        model.addAttribute("message", e.getMessage());

        return "error/default";

    }
}
