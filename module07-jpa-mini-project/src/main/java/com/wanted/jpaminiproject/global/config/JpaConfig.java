package com.wanted.jpaminiproject.global.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.wanted.jpaminiproject")
@EntityScan(basePackages = "com.wanted.jpaminiproject")
public class JpaConfig {

}
