package com.jeongbeom.ecommerce.cart.exception;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;

public class InvalidCartQuantityException extends CustomException {

    public InvalidCartQuantityException() {
        super(ErrorCode.INVALID_CART_QUANTITY);
    }
}
