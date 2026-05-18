package com.wanted.springasync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/* comment.
*   스케쥴러 사용 가능하게 하는 어노테이션
* */
@EnableScheduling
@SpringBootApplication
public class Module02SpringAsyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(Module02SpringAsyncApplication.class, args);
    }

}
