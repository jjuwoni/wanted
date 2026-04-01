package com.wanted.container.section01;

import com.wanted.container.section01.service.PaymentService;

public class Application {
    public static void main(String[] args) {

        System.out.println("==========스프링 없이 객체를 직접 생성해서 사용==========");

        /* comment. 결제 시스템이  있다고 가정을 해본다.
        *   KaKaoPay 와 NaverPay 가 있으며 Application 에서는
        *   PaymentService 를 호출 시 결제 플랫폼 객체를 생성해서
        *   결제가 되는 시나리오를 구성해본다.
        * */

        // 객체 생성
        PaymentService paymentService = new PaymentService();

        String orderId = "order-001";
        double amount = 150000.0;
        boolean result = paymentService.processPayment(orderId, amount);

        System.out.println("결제 결과 : " + (result ? "성공" : "실패"));
        System.out.println("=====================================");

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
