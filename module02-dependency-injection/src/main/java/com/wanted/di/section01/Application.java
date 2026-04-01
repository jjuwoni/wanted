package com.wanted.di.section01;

import com.wanted.di.section01.config.AppConfig;
import com.wanted.di.section01.service.PaymentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {

        /* comment.
        *   AppConfig 파일에서 IoC 컨테이너가 관리하는 Bean 을 등록했었다.
        *   문제점.
        *   1. Bean 을 등록할 때 1개라도 실수로 누락한다면?
        *   2. Bean 이 100개가 넘어간다면?
        * */
        System.out.println("==========컴포넌트 스캔 테스트==========");

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        PaymentService paymentService = context.getBean("paymentService", PaymentService.class);
        paymentService.processPayment("order-002", 200);
    }
}
