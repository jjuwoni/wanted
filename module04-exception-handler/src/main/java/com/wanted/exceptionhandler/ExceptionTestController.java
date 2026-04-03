package com.wanted.exceptionhandler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExceptionTestController {

    @GetMapping("nullpointer")
    public String nullPointer() {
        // 고의적으로 nullPointerException 발생시킴!
        String str = null;
        System.out.println(str.toString());

        return "/";
    }

    /* comment.
    *   @ExceptionHandler(처리하고자 하는 예외) 정의를 해두면
    *   정의한 예외상황이 발생하게 되면, ExceptionHandler 가
    *   흐름을 가로채서 동작을 하게 된다.
    * */
    @ExceptionHandler(NullPointerException.class)
    public String nullHandler(Model model, NullPointerException e) {

        model.addAttribute("key", e.getMessage());

        return "error/nullPointer";
    }

    @GetMapping("userexception")
    public String userException() throws MemberNotFoundException {
        boolean check = true;
        if(check) {
            throw new MemberNotFoundException("석범님 어디갔어요..아프지마요");
        }

        return "/";
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public String userException(Model model, MemberNotFoundException e) {

        model.addAttribute("key", e);

        return "error/memberNotFound";
    }

}
