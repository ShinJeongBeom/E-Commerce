package com.jeongbeom.ecommerce.payment.entity;

import com.jeongbeom.ecommerce.common.entity.BaseTimeEntity;
import com.jeongbeom.ecommerce.order.entity.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, unique = true, length = 200)
    private String paymentKey;

    @Column(nullable = false, unique = true, length = 100)
    private String tossOrderId;

    @Column(nullable = false, length = 255)
    private String orderName;

    @Column(nullable = false)
    private long totalAmount;

    @Column(nullable = false, length = 30)
    private String method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(nullable = false)
    private OffsetDateTime requestedAt;

    private OffsetDateTime approvedAt;

    @Column(length = 500)
    private String receiptUrl;

    @Column(length = 100)
    private String failureCode;

    @Column(length = 500)
    private String failureMessage;

    public Payment(
            Order order,
            String paymentKey,
            String tossOrderId,
            String orderName,
            long totalAmount,
            String method,
            PaymentStatus status,
            OffsetDateTime requestedAt,
            OffsetDateTime approvedAt,
            String receiptUrl
    ) {
        this.order = order;
        this.paymentKey = paymentKey;
        this.tossOrderId = tossOrderId;
        this.orderName = orderName;
        this.totalAmount = totalAmount;
        this.method = method;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.receiptUrl = receiptUrl;
    }

    public void changeStatus(PaymentStatus status) {
        this.status = status;
    }

    public void markFailed(PaymentStatus status, String failureCode, String failureMessage) {
        this.status = status;
        this.failureCode = failureCode;
        this.failureMessage = failureMessage;
    }

    public void updateReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }
}
