package com.jeongbeom.ecommerce.order.exception;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;

public class OrderAlreadyCancelledException extends CustomException {

    public OrderAlreadyCancelledException() {
        super(ErrorCode.ORDER_ALREADY_CANCELLED);
    }
}
