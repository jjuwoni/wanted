package com.wanted.container.section03.service;

import com.wanted.container.section03.gateway.PaymentInterface;

public class PaymentService {

    /* 싱글톤 스코프 VS 프로토타입 스코프 확인을 위한
    *   상태 선언!!
    * */
    private String lastOrderId;
    private final PaymentInterface paymentInterface;

    public PaymentService(PaymentInterface pamentInterface) {
        this.paymentInterface = pamentInterface;
    }

   // lastOrderId 가져오기 위한 getter 메소드
    public String getLastOrderId() {
        return lastOrderId;
    }

    public boolean processPayment(String orderId, double amount) {
        System.out.println("결제 비즈니스 로직 시작. 주문 ID = " + orderId + ", 금액 = " + amount);
        this.lastOrderId = orderId;

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
