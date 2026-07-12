package com.jeongbeom.ecommerce.payment.dto;

import java.time.OffsetDateTime;

public record TossPaymentConfirmResponse(
        String paymentKey,
        String orderId,
        String orderName,
        String method,
        String status,
        long totalAmount,
        OffsetDateTime requestedAt,
        OffsetDateTime approvedAt,
        TossReceipt receipt
) {

    public record TossReceipt(String url) {
    }
}
