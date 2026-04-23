package com.jeongbeom.ecommerce.auth.controller;

import com.jeongbeom.ecommerce.auth.dto.LoginRequestDto;
import com.jeongbeom.ecommerce.auth.dto.LoginResponseDto;
import com.jeongbeom.ecommerce.auth.jwt.JwtUtil;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

        private final MemberRepository memberRepository;
        private final JwtUtil jwtUtil;

        @PostMapping("/login")
        public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {

            Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                    .orElseThrow(MemberNotFoundException::new);

            if (!member.getPassword().equals(loginRequestDto.getPassword())) {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }

            String token = jwtUtil.createToken(member.getId());

            return ResponseEntity.ok(new LoginResponseDto(token));

    }
}
