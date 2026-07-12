package com.jeongbeom.ecommerce.payment.entity;

public enum PaymentStatus {
    READY,             // 결제 준비
    IN_PROGRESS,       // 결제 인증 완료, 승인 전
    DONE,              // 결제 승인 완료
    CANCELED,          // 결제 취소
    PARTIAL_CANCELED,  // 부분 취소
    ABORTED,           // 결제 승인 실패
    EXPIRED            // 결제 만료
}
