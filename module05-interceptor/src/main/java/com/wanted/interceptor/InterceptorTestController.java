package com.wanted.interceptor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InterceptorTestController {

    @GetMapping("/stopwatch")
    public String handleMethod() throws InterruptedException {

        System.out.println("stopwatch 요청을 처리하는 핸들러메소드 호출됨...");

        // 아무것도 수행하지 않으면 소요 시간이 0으로 나올 수 있기 때문에
        // 의도적으로 2초간 정지
        Thread.sleep(2000);

        return "result";
    }
}
