package com.wanted.jpaminiproject.global.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.wanted.jpaminiproject")
public class ContextConfig {

    /* comment.
    *   modelMapper 란?
    *   Entity 클래스는 데이터의 무결성을 위해 Setter 사용을 지양한다.
    *   그렇다면 어떻게 DTO <-> Entity 값을 set 할까?
    *   여러 가지 방법이 있는데 대표적인 방식으로 modelmapper 라는 것이 있다.
    * */

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        // 우리가 사용할 ModelMapper 설정하기
        modelMapper.getConfiguration()
                // private 필드에 접근할 수 있게 권한 부여
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                // DTO 필드와 Entity 필드 매칭 가능 여부 설정
                .setFieldMatchingEnabled(true);

        return modelMapper;
    }

}
