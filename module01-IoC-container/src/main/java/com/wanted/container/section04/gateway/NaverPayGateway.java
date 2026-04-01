package com.wanted.container.section04.gateway;

public class NaverPayGateway implements PaymentInterface {

    /*
     * 결제를 담당하는 메서드
     * @param orderId 주문 ID
     * @param amount 결제 금액
     * @return 결제 성공여부
     * */
    @Override
    public boolean processPayment(String orderId, double amount) {
        System.out.println("네이버페이로 결제 진행 시작 : 주문ID = " + orderId + ", 금액 = " + amount);
        return true;
    }
}
