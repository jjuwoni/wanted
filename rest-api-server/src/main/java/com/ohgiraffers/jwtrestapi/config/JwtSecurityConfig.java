package com.ohgiraffers.jwtrestapi.config;

import com.ohgiraffers.jwtrestapi.jwt.JwtFilter;
import com.ohgiraffers.jwtrestapi.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/* 설명. JWT 관련 필터를 UsernamePasswordAuthenticationFilter 앞 쪽에 추가 */
@Configuration
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    @Autowired
    public JwtSecurityConfig(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        // 우리가 직접 만든 커스텀 필터인 JwtFilter
        JwtFilter customFilter = new JwtFilter(tokenProvider);
        // JwtFilter를 Filterchain에 추가
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
