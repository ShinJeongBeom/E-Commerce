package com.jeongbeom.ecommerce.auth.service;

import com.jeongbeom.ecommerce.auth.dto.LoginRequestDto;
import com.jeongbeom.ecommerce.auth.dto.LoginResponseDto;
import com.jeongbeom.ecommerce.auth.dto.SignupRequestDto;
import com.jeongbeom.ecommerce.auth.jwt.JwtUtil;
import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.seller.entity.SellerApprovalStatus;
import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import com.jeongbeom.ecommerce.seller.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByLoginId(loginRequestDto.getLoginId())
                .orElseThrow(MemberNotFoundException::new);

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(member.getId());

        return new LoginResponseDto(token, member.getLoginId(), member.getRole().name());
    }

    public boolean isLoginIdAvailable(String loginId) {
        return !memberRepository.existsByLoginId(loginId);
    }

    public void signup (SignupRequestDto signupRequestDto){
        if (memberRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByLoginId(signupRequestDto.getLoginId())) {
            throw new CustomException(ErrorCode.DUPLICATE_LOGIN_ID);
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        Role role = resolveSignupRole(signupRequestDto.getRole());

        Member member = new Member(
                signupRequestDto.getEmail(),
                signupRequestDto.getLoginId(),
                encodedPassword,
                signupRequestDto.getPhone(),
                role
        );
        memberRepository.save(member);

        if (role == Role.SELLER) {
            SellerProfile sellerProfile = new SellerProfile(
                    member,
                    signupRequestDto.getLoginId() + " 스토어",
                    SellerApprovalStatus.PENDING
            );
            sellerProfileRepository.save(sellerProfile);
        }
    }

    private Role resolveSignupRole(String role) {
        if ("SELLER".equalsIgnoreCase(role)) {
            return Role.SELLER;
        }

        return Role.USER;
    }
}
