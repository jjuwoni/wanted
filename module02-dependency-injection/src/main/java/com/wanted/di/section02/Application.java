package com.wanted.di.section02;

import com.wanted.di.section02.config.AppConfig;
import com.wanted.di.section02.service.PaymentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {

       /* comment.
       *    DI : 의존성 주입
       *    Spring IoC 컨테이너에서 객체간의 의존성을 개발자가 관리하지 않고,
       *    IoC 컨테이너가 자동으로 주입해주는 매커니즘.
       * */

        System.out.println("==========의존성 주입 테스트==========");

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        PaymentService paymentService = context.getBean("paymentService", PaymentService.class);
        paymentService.processPayment("order-002", 200);
        System.out.println("============================================");
        PaymentService paymentService2 = context.getBean("naverPay", PaymentService.class);
        paymentService2.processPayment("order-002", 200);
    }
}
