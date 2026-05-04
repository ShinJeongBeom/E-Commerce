package com.jeongbeom.ecommerce.product.exception;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;

public class NotEnoughStockException extends CustomException {

    public NotEnoughStockException() {
        super(ErrorCode.NOT_ENOUGH_STOCK);
    }
}
