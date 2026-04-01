package com.wanted.di.section02.gateway;

import org.springframework.stereotype.Component;

/* comment.
*   @Component 어노테이션
*   - Spring 이 해당 클래스를 컴포넌트로 인식하여, Bean 으로 등록할 수 있도록 지시.
*   - 역할 : 일반적인 Bean 을 정의하여, IoC 컨테이너가 관리할 객체임을 명시한다.
*   - Bean 이름 : 기본값은 클래스명 첫글자 소문자 ex) naverPayGateway
*   - 사용 예시 : 독립적인 기능 모듈 (ex : 결제 등 외부 상호작용 객체, 유틸리티 클래스)
* */
@Component
// 기본 Bean 이름은 naverPayGateway
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
