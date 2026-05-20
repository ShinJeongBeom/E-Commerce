package com.jeongbeom.ecommerce.cart.exception;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;

public class CartNotFoundException extends CustomException {

    public CartNotFoundException() {
        super(ErrorCode.CART_NOT_FOUND);
    }
}
