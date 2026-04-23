package com.jeongbeom.ecommerce.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    // 토큰 서명용 비밀키
    private final String secretKey;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long expiration

    ) {

        this.secretKey = secretKey;
        this.expiration = expiration;

    }

    // 문자열을 JWT 서명용 키 객체로 변환
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    }

    //로그인 성공시 토큰 생성
    public String createToken(Long memberId) {
        return Jwts.builder()
                .setSubject(memberId.toString())    // 이 토큰의 주인이 누구인지
                .setIssuedAt(new Date())            // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiration))   // 토큰 만료 시간 : 1시간
                .signWith(getSigningKey())         // 토큰 서명
                .compact();
    }

    // 요청에 들어온 JWT를 해석해서 memberId를 꺼냄 기존 그냥 memberId -> 현재 JWT(memberId)
    public Long getMemberId(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }
}
