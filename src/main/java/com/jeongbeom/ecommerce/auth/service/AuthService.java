package com.jeongbeom.ecommerce.auth.service;

import com.jeongbeom.ecommerce.auth.dto.LoginRequestDto;
import com.jeongbeom.ecommerce.auth.dto.LoginResponseDto;
import com.jeongbeom.ecommerce.auth.dto.SignupRequestDto;
import com.jeongbeom.ecommerce.auth.jwt.JwtUtil;
import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(member.getId());

        return new LoginResponseDto(token);
    }

    public void signup (SignupRequestDto signupRequestDto){
        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        Member member = new Member(
                signupRequestDto.getEmail(),
                encodedPassword,
                signupRequestDto.getPhone(),
                Role.USER
        );
        memberRepository.save(member);
    }
}
