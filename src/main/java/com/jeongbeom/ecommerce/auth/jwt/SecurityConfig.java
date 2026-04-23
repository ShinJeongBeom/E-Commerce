package com.jeongbeom.ecommerce.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())           //현재 postman 기반 개발이므로 CSRF 끔
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()                //로그인 API는 토큰 없이 접근 허용
                        .anyRequest().authenticated()                             // 그 외 모든 요청 인증 필요
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터를 기본 인증 필터보다 먼저 실행
        return http.build();

    }

}