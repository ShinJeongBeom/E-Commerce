package com.jeongbeom.ecommerce.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String code;        // 시스템
    private String message;     // 사람
}