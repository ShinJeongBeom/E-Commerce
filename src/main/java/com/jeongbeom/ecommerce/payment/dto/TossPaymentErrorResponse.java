package com.jeongbeom.ecommerce.payment.dto;

public record TossPaymentErrorResponse(
        String code,
        String message
) {
}
