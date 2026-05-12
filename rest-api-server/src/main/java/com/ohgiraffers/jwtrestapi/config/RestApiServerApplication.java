package com.ohgiraffers.jwtrestapi.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.ohgiraffers.jwtrestapi")
public class RestApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApiServerApplication.class, args);
    }

}
