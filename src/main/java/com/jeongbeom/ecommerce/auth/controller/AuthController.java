package com.jeongbeom.ecommerce.auth.controller;

import com.jeongbeom.ecommerce.auth.dto.LoginRequestDto;
import com.jeongbeom.ecommerce.auth.dto.LoginResponseDto;
import com.jeongbeom.ecommerce.auth.dto.SignupRequestDto;
import com.jeongbeom.ecommerce.auth.service.AuthService;
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

        private final AuthService authService;

        @PostMapping("/login")
        public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
            return ResponseEntity.ok(authService.login(loginRequestDto));

    }
        @PostMapping("/signup")
        public ResponseEntity<String> signup(@RequestBody SignupRequestDto signupRequestDto){
            authService.signup(signupRequestDto);
            return ResponseEntity.ok().build();
        }
}
