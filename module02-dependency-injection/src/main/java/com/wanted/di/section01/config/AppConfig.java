package com.wanted.di.section01.config;

import com.wanted.di.section01.service.PaymentService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "com.wanted.di.section01",
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
}
