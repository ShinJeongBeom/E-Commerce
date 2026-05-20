package com.jeongbeom.ecommerce.common.exception;

import lombok.Getter;

@Getter
//런타임 예외로 관리
public class CustomException extends RuntimeException{

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
