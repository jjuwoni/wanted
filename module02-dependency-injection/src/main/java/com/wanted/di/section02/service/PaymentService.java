package com.wanted.di.section02.service;

import com.wanted.di.section02.gateway.NaverPayGateway;
import com.wanted.di.section02.gateway.PaymentInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* comment.
*   @Service
*   - @Component 의 특수한 형태다. 서비스 계층에 속하는 Bean 을 명시한다.
*   - 역할 : 비즈니스 로직을 처리하는 클래스
* */

@Service
public class PaymentService {

    private final PaymentInterface paymentInterface;

    /* comment.
    *   @Autowired : 스프링의 핵심 어노테이션 중 하나이며
    *   객체간 의존성을 자동으로 주입해주는 역할을 하게 된다.
    *   ex) controller -> service -> dao
    *   - Service 에서 PaymentInterface 를 의존하려고 할 때 Error 가
    *   - 발생하는 이유는 PaymentInterface 를 상속받은 구현체가 2개이기
    *   - 때문에 어떤 Bean 을 주입해야 할 지 모르기 때문에 발생하는 Error 이다.
    * */
    @Autowired
    public PaymentService(PaymentInterface paymentInterface) {
        this.paymentInterface = paymentInterface;
    }

    public boolean processPayment(String orderId, double amount) {
        System.out.println("결제 비즈니스 로직 시작. 주문 ID = " + orderId + ", 금액 = " + amount);

        // 결제 게이트웨이를 통한 결제 처리
        boolean result = paymentInterface.processPayment(orderId,amount);

        if(result) {
            System.out.println("결제 처리가 성공적으로 마무리!");
        } else {
            System.out.println("결제 처리 실패...🚨🚨🚨");
        }

        return result;
    }

}
