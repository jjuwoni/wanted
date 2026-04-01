package com.wanted.di.section03.service.field;

import com.wanted.di.section03.gateway.PaymentInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceField {

    /* comment.
    *   @Autowired 어노테이션을 필드에 작성하는 방식.
    *   장점
    *   - 코드가 간결 : 생성자 없이 주입이 가능하다.
    *   - 빠른 구현 : 서비스 프로토타입 개발 시 유용하다.
    *   단점
    *   - final 키워드 사용 불가 : 불변성을 보장할 수 없다.
    *   - 또한 필드에 다른 값을 대입하더라도 컴파일 시점에 Error 가 발생하지 않는다.
    *   - 의존성 주입의 시점이 불명확하다. : 객체가 생성된 후 어느 시점에 의존성이 추가될 지 개발자는 모른다.
    *   - 의존성이 숨겨진다. 필수 의존성이 코드상으로 나오지 않는다.
    *   - 해당 객체가 생성되는 시점에는 PaymentInterface 를 주입 받지 않고, 추후에 추가된다.
    * */

    @Autowired
    private PaymentInterface paymentInterface;

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
