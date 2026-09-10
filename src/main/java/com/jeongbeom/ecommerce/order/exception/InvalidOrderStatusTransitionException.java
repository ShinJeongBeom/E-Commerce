package com.jeongbeom.ecommerce.order.exception;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;

public class InvalidOrderStatusTransitionException extends CustomException {

    public InvalidOrderStatusTransitionException() {
        super(ErrorCode.INVALID_ORDER_STATUS_TRANSITION);
    }
}
