package com.jeongbeom.ecommerce.auth.dto;

import lombok.Getter;

@Getter
public class SignupRequestDto {

    private String email;
    private String password;
    private String phone;
}
