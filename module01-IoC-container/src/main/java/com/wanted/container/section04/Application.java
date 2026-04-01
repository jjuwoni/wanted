package com.wanted.container.section04;

import com.wanted.container.section04.config.AppConfig;
import com.wanted.container.section04.service.PaymentService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Application {
    public static void main(String[] args) {

        /* comment.
        *   Bean 의 생명주기를 알면 좋은 이유!
        *   - 1. 리소스 관리 최적화
        *   - Bean 의 생성, 초기화, 소멸 단계를 알면 리소스(ex-DB연결, file 핸들링)를
        *   - 적절한 시점에 할당하고 해제할 수 있다.
        *   - 2. 어플리케이션 동작 예측
        *   - 생명주기 단계를 이해하면 Bean 생성 시점, 사용 준비 완료 시점, 소멸 시점 예측이 가능하다.
        *   - 3. 커스터마이징 가능
        *   - 특정한 시점(소멸, 생성)에 로직을 추가할 수 있다.
        *   - ex) 결제 인증을 초기화 시점에 수행, 결제 로그를 소멸 시점에 정리 등
        * */

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        System.out.println("=============라이프 사이클 테스트=======================");

        PaymentService paymentServiceSingle = context.getBean("paymentServiceSingle", PaymentService.class);

        String orderId = "order-001";
        double amount = 150000.0;
        boolean result = paymentServiceSingle.processPayment(orderId, amount);

        ((AnnotationConfigApplicationContext) context).close();
        System.out.println("=====================================");

    }
}
