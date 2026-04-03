package com.wanted.exceptionhandler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GlobalExceptionTestController {

    /* comment.
    *   ExceptionTestController 에 작성한 핸들러가 동작을 하지 않는다.
    *   이 이유는 해당 컨트롤러와 현재 컨트롤러는 전혀 연관없는 클래스이다.
    * */

    @GetMapping("global-nullpointer")
    public String globalNull() {
        String str = null;
        // null 발생 포인트!
        System.out.println(str.toString());

        return "/";
    }

    @GetMapping("global-userexception")
    public String globalMember() throws MemberNotFoundException {
        boolean check = true;
        if(check) {
            throw new MemberNotFoundException("돌아와~~~~~");
        }

        return "/";
    }

    @GetMapping("global-default")
    public String noExpected() {
        int[] iarr = new int[0];
        System.out.println(iarr[1]);

        return "/";
    }
}
