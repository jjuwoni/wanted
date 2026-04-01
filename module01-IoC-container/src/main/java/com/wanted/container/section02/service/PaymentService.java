package com.wanted.container.section02.service;

import com.wanted.container.section02.gateway.NaverPayGateway;
import com.wanted.container.section02.gateway.PaymentInterface;

public class PaymentService {

    private final PaymentInterface paymentInterface;

    public PaymentService(PaymentInterface pamentInterface) {
        this.paymentInterface = pamentInterface;
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
