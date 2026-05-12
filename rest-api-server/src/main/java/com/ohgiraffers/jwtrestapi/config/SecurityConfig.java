package com.ohgiraffers.jwtrestapi.config;

import com.ohgiraffers.jwtrestapi.jwt.JwtAccessDeniedHandler;
import com.ohgiraffers.jwtrestapi.jwt.JwtAuthenticationEntryPoint;
import com.ohgiraffers.jwtrestapi.jwt.JwtFilter;
import com.ohgiraffers.jwtrestapi.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰을 발급하고 검증하는 Token Provider
    private final TokenProvider tokenProvider;
    // 인증 실패 관련 예외
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    // 접근 거부 관련 예외
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

//    @Autowired
//    public SecurityConfig(TokenProvider tokenProvider,
//                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
//                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
//        this.tokenProvider = tokenProvider;
//        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
//        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
//    }

    /* 목차. 1. 암호화 처리를 위한 PasswordEncoder를 빈으로 설정(빈을 등록 시 메소드 이름 오타 없도록 주의!) */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* 목차. 2. Spring Security 설정을 무시 할 정적 리소스 등록 */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers("/css/**", "/js/**", "/images/**", "/lib/**", "/productimgs/**");
    }

    /* 목차. 3. HTTP요청에 대한 권한별 설정 (세션 인증 -> 토큰 인증으로 인해 바뀐 부분 존재) */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. CSRF 보호 비활성화
                .csrf(csrf -> csrf.disable())
                // 2. 예외 처리
                .exceptionHandling(exception -> {
                    // 필요한 권한이 없을 때 403(Forbidden)을 반환
                    exception.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                    // 인증되지 않은 접근 시 401(Unauthorized)를 반환
                    exception.accessDeniedHandler(jwtAccessDeniedHandler);
                })
                // 3. HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(auth -> {
                    /* 설명.
                     *  CORS를 위해 preflight 요청 처리용 options 요청 허용.
                     *  ====================================================================================================
                     *  preflight request란?
                     *  요청 할 url이 외부 도메인일 경우 웹 브라우저에서 자체 실행되며 options 메소드로 사전 요청을 보내게 된다.
                     *  사전에 요청이 안전한지 확인하기 위함(유효한지 서버에 미리 파악할 수 있도록 보내는 수단이다.)
                     * */
                    // CORS Preflight 요청 허용
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    // root 경로는 인증 필요
                    auth.requestMatchers("/").authenticated();
                    // 특정 경로는 무조건 허용
                    auth.requestMatchers("/auth/**", "/api/v1/products/**", "/api/v1/reviews/**").permitAll();
                    // Swagger API 문서 허용
                    auth.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    // API 경로는 USER 또는 ADMIN 역할을 가진 사용자만 접근 가능
                    auth.requestMatchers("/api/**").hasAnyRole("USER", "ADMIN");
                    /* 설명. 아래는 프로젝트 초기 구현시, Security 기능을 약화시켜 개발을 진행하게 끔 해주는 내용들이다. */
                    // 어떤 요청이든 허용 -> Security를 활용한 로그인이 모두 완성되지 않았을 때 사용할 것
//				auth.anyRequest().permitAll();
                })
                // 4. 세션 방식을 사용하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 5. 기본 CORS 설정 사용
                .cors(cors -> {})
                // 6. 우리가 직접 작성한 커스텀 필터인 JwtFilter를 필터 체인에 추가
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* 목차. 4. CORS 설정용 Bean(허용 할 origin과 httpMethod 종류와 header 값) */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        // CORS 관련 설정을 진행할 객체 생성
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 도메인
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // 허용할 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
        // 허용할 헤더
        configuration.setAllowedHeaders(
                Arrays.asList(
                        // 서버에서 응답할 때, 어떤 출처(origin)에서 요청을 허용할지를 결정하는 헤더
                        "Access-Control-Allow-Origin",
                        // 요청 또는 응답의 콘텐츠 유형(미디어 타입)
                        "Content-type",
                        // CORS 요청을 통해 허용되는 HTTP 요청 헤더
                        "Access-Control-Allow-Headers",
                        // 클라이언트가 서버로 인증 정보(여기서는 JWT)를 보내기 위해 사용되는 헤더
                        "Authorization",
                        //XMLHttpRequest로 요청이 이루어졌는지를 나타내기 위해 사용
                        "X-Requested-With"
                )
        );

        // CORS 설정을 적용할 URL 패턴 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
