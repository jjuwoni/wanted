package com.wanted.container.section01.gateway;

/*
* 카카오페이 결제 게이트웨이 구현체
* 실제 결제 처리를 담당하는 클래스
* */

public class KaKaoPayGateway {

    /*
    * 결제를 담당하는 메서드
    * @param orderId 주문 ID
    * @param amount 결제 금액
    * @return 결제 성공여부
    * */
    public boolean pay(String orderId, double amount) {
        System.out.println("카카오페이로 결제 진행 시작 : 주문ID = " + orderId + ", 금액 = " + amount);
        return true;
    }
}
