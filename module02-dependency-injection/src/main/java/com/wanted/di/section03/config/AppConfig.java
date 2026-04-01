package com.wanted.di.section03.config;

import com.wanted.di.section03.gateway.PaymentInterface;
import com.wanted.di.section03.service.PaymentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.wanted.di.section03")
public class AppConfig {


}
