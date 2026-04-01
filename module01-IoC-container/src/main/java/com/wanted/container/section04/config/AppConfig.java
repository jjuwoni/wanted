package com.wanted.container.section04.config;

import com.wanted.container.section04.gateway.KaKaoPayGateway;
import com.wanted.container.section04.gateway.NaverPayGateway;
import com.wanted.container.section04.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    /* comment.
    *   Spring Bean 의 생명주기 확인
    *   - 주요 단계
    *   1. 인스턴스 생성 : 컨테이너가 PaymentService 를 호출한다. (new 키워드 사용)
    *   2. 의존성 주입 : 생성자를 통해 PaymentInterface(naverPay) 가 주입된다.
    *   3. 초기화 : @Bean 의 initMethod="init" 으로 지정된 메서드가 호출되어 초기 설정 수행
    *   4. 사용 : 어플리케이션에서 getBean()
    *   5. 소멸 : @Bean 의 destroyMethod="destroy" 로 지정된 메서드가 호출되어 리소스 정리
    * */

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public PaymentService paymentServiceSingle() {
        return new PaymentService(naverPayGateway());
    }

    @Bean
    public KaKaoPayGateway kakaoPayGateway() {
        return new KaKaoPayGateway();
    }

    @Bean
    public NaverPayGateway naverPayGateway() {
        return new NaverPayGateway();
    }


}
