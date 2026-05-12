package com.ohgiraffers.jwtrestapi.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                // private 필드에 접근 하기 위한 설정
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                // DTO , Entity 필드 접근 가능 설정
                .setFieldMatchingEnabled(true);
        return modelMapper;
    }
}
