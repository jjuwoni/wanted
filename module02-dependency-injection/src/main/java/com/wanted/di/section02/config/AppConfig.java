package com.wanted.di.section02.config;

import com.wanted.di.section02.gateway.PaymentInterface;
import com.wanted.di.section02.service.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.wanted.di.section02",
        excludeFilters = @ComponentScan.Filter(
//                type = FilterType.ASSIGNABLE_TYPE, classes = {PaymentService.class}
//                type = FilterType.ANNOTATION, classes = {org.springframework.stereotype.Service.class}
//                type = FilterType.REGEX, pattern = {"com.wanted.di.section01.service.*"}
        ))
public class AppConfig {

    /* comment.
    *   - 지정된 패키지에서 @Component 관련 어노테이션이 붙은 클래스를
    *   - 스캔하여 Bean 으로 등록할 수 있게 해준다.
    *   - 장점
    *   - 코드 간소화 : @Bean 으로 일일이 등록할 필요가 없어진다.
    *   - 유연성 : 새로운 컴포넌트 추가 시 설정 변경 없이 자동 인식.
    *   - @ComponentScan 시 경로를 작성하지 않으면, 자신이 포함된 디렉토리는 default로 인식한다.
    *   - excludeFilter 를 사용하게 되면, 사용자 정의의 클래스, 어노테이션, 정규표현식으로
    *   - IoC 컨테이너 Bean 등록에서 제외할 수 있다.
    * */
    /* comment.
    *   @Qualifire 어노테이션은 특정 Bean 을 이름으로 지정을 한다.
    *   @Primary 설정으로 default 값이 KakaoPay로 되어 있지만,
    *   @Qualifire 어노테이션은 우리가 명시적으로 Interface 의 구현체를 지정할 수 있다.
    *   의존성 주입 우선순위는 @Qualifier 가 @Primary 보다 높다.
    * */
    @Bean("naverPay")
    public PaymentService naverPayment(@Qualifier("naverPayGateway") PaymentInterface paymentInterface) {
            return new PaymentService(paymentInterface);
    }
}
