package com.jeongbeom.ecommerce.payment.dto;

import com.jeongbeom.ecommerce.payment.entity.Payment;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class PaymentResponse {

    private final Long paymentId;
    private final Long orderId;
    private final String paymentKey;
    private final String tossOrderId;
    private final String orderName;
    private final long totalAmount;
    private final String method;
    private final String status;
    private final OffsetDateTime requestedAt;
    private final OffsetDateTime approvedAt;
    private final String receiptUrl;

    public PaymentResponse(Payment payment) {
        this.paymentId = payment.getId();
        this.orderId = payment.getOrder().getId();
        this.paymentKey = payment.getPaymentKey();
        this.tossOrderId = payment.getTossOrderId();
        this.orderName = payment.getOrderName();
        this.totalAmount = payment.getTotalAmount();
        this.method = payment.getMethod();
        this.status = payment.getStatus().name();
        this.requestedAt = payment.getRequestedAt();
        this.approvedAt = payment.getApprovedAt();
        this.receiptUrl = payment.getReceiptUrl();
    }
}
