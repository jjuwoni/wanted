package com.wanted.di.section03.service.constructor;

import com.wanted.di.section03.gateway.PaymentInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceConstructor {

    // final 키워드의 특징
    // 불변성(상수)이다. 따라서 final 키워드를 사용하려면 반드시 초기화를
    // 필드에서 해주거나, 생성자를 통해 초기화를 해주어야 한다.
    private final PaymentInterface paymentInterface;

    /* comment.
    *   @Autowired 라는 어노테이션은 IoC 컨테이너에서 의존성이 필요한 경우에
    *   자동으로 매개변수부에 있는 Bean 을 찾아서 주입해준다.
    *   생성자 주입의 장점
    *   - final 키워드로 의존성이 변경되는 것을 방지한다. (불변성 보장)
    *   - 필수 의존성 강제화 : final 키워드의 특징 초기화를 반드시 해야한다는 개념 때문에
    *   - 의존성을 객체가 생성되고 나중에 넣어주는 것이 아닌, 생성 시점에 바로 넣어준다.
    *   - 의존성 명확 : 코드만 보더라도, 해당 클래스가 어떤 클래스를 의존하는 지 알 수 있다.
    *   생성자 주입의 단점
    *   - 의존성이 많을 시에 생성자 매개변수부가 길어진다.
    *
    *   생성자 주입 방식은 Spring 환경에서 공식적으로 권장되는 방식이다.
    * */

    @Autowired
    public PaymentServiceConstructor(PaymentInterface paymentInterface) {
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
