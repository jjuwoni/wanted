package com.wanted.container.section02;

import com.wanted.container.section02.config.AppConfig;
import com.wanted.container.section02.service.PaymentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {

       /* comment.
       *    IoC Container (제어의 역전)
       *    section01 에서는 개발자가 직접 new 키워드를 사용해서
       *    인스턴스를 생성했다.
       *    이렇게 되면, 객체 간 결합도가 높아지게 되며, 유연성이 떨어지고,
       *    테스트 및 유지보수가 어렵다는 문제가 발생하게 된다.
       * */

        /* comment.
        *   IoC Container == Application Context == Bean Factory
        * */
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // getBean() : 컨테이너에 등록 된 객체를 꺼낸다.
        PaymentService paymentService = context.getBean("paymentService", PaymentService.class);

        String orderId = "order-001";
        double amount = 150000.0;
        boolean result = paymentService.processPayment(orderId, amount);

        System.out.println("결제 결과 : " + (result ? "성공" : "실패"));
        System.out.println("=====================================");

        // @Bean 이 싱글톤 인스턴스인지 확인하는 구문
        PaymentService paymentService2 = context.getBean(PaymentService.class);

        System.out.println("payment == payment2 : " + (paymentService == paymentService2));

        /* comment.
        *   결제 게이트웨이를 만약 KaKao -> Naver 바꾸면 어떻게 될까?
        *   문제점
        *   1. 결제 게이트웨이 구현체를 직접 변경해야 한다.
        *   2. 메소드 일치를 맞춰줘야 한다.
        *   3. 만약 전달인자 갯수가 달랐다면 이 부분도 수정해야 한다.
        *   "개방-폐쇄 원칙(OCP)를 위반하며, 1개의 수정 시 여러 코드 수정이
        *   필요하므로 코드의 유연성이 떨어지게 된다.
        *   소프트웨어 요소는 확장에는 열려 있어야 하며, 변경에는 닫혀 있어야 한다.
        * */

    }
}
