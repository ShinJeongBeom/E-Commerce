package com.jeongbeom.ecommerce.order.exception;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;

public class OrderAccessDeniedException extends CustomException {

    public OrderAccessDeniedException() {
        super(ErrorCode.ORDER_ACCESS_DENIED);
    }
}
