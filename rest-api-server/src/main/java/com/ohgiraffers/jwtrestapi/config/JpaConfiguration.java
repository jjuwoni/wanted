package com.ohgiraffers.jwtrestapi.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/* 설명.
 *  엔티티 및 레포지토리 인터페이스를 Bean(= Entity)으로 인식시키기 위해 필요한 설정.
 *  해당 설정을 깜빡하고 지나치는 경우가 많으니 미리 만들어두자.
 * */
@Configuration
@EntityScan(basePackages = "com.ohgiraffers.jwtrestapi")
@EnableJpaRepositories(basePackages = "com.ohgiraffers.jwtrestapi")
public class JpaConfiguration {
}
