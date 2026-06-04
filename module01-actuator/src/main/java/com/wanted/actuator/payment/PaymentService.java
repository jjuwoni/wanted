package com.wanted.actuator.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public void pay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("결제 처리가 중단되었습니다.", exception);
        }
    }
}
