package com.jeongbeom.ecommerce.product.exception;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;

public class InvalidStockQuantityException extends CustomException {

    public InvalidStockQuantityException() {
        super(ErrorCode.INVALID_STOCK_QUANTITY);
    }
}