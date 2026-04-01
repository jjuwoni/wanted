package com.wanted.container.section03;

import com.wanted.container.section03.config.AppConfig;
import com.wanted.container.section03.service.PaymentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("=============싱글톤 스코프 테스트=======================");
        // getBean() : 컨테이너에 등록 된 객체를 꺼낸다.
        PaymentService singlePay = context.getBean("singlePay", PaymentService.class);

        String orderId = "order-001";
        double amount = 150000.0;
        boolean result = singlePay.processPayment(orderId, amount);
        System.out.println("singlePay 의 마지막 주문 ID : " + singlePay.getLastOrderId());

        PaymentService singlePay2 = context.getBean("singlePay", PaymentService.class);
        System.out.println("singlePay2 의 마지막 주문 ID : " + singlePay2.getLastOrderId());

        System.out.println("=====================================");

        System.out.println("=============프로토타입 스코프 테스트=======================");
        // getBean() : 컨테이너에 등록 된 객체를 꺼낸다.
        PaymentService protoPay = context.getBean("protoPay", PaymentService.class);

        String orderId2 = "order-001";
        double amount2 = 150000.0;
        boolean result2 = protoPay.processPayment(orderId, amount);
        System.out.println("protoPay 의 마지막 주문 ID : " + protoPay.getLastOrderId());

        PaymentService protoPay2 = context.getBean("protoPay", PaymentService.class);
        System.out.println("protoPay2 의 마지막 주문 ID : " + protoPay2.getLastOrderId());

    }
}
