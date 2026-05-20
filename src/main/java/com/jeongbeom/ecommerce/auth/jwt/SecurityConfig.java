package com.jeongbeom.ecommerce.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                        .requestMatchers("/auth/**").permitAll()                //회원가입&로그인 API는 누구나 가능

                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()        // 상품 조회는 누구나 가능
                        .requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")     // 상품등록은 ADMIN만 가능
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")   // 상품 수정은 ADMIN만 가능
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")// 상품 삭제는 ADMIN만 가는ㅇ
                        .anyRequest().authenticated()                             // 그 외 모든 요청 로그인 필요
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터를 기본 인증 필터보다 먼저 실행
        return http.build();

    }
    //  비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}