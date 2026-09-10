package com.jeongbeom.ecommerce.order.entity;

public enum OrderStatus {
    CREATED,    //주문 생성됨 (결제전)
    PAID,       //결제 완료
    PREPARING,  //상품 준비중
    SHIPPED,    //배송 시작
    DELIVERED,  //배송 완료
    CANCELLED;  //주문 취소

    public boolean canTransitionTo(OrderStatus nextStatus) {
        if (nextStatus == null) {
            return false;
        }

        return switch (this) {
            case CREATED -> nextStatus == PAID || nextStatus == CANCELLED;
            case PAID -> nextStatus == PREPARING;
            case PREPARING -> nextStatus == SHIPPED;
            case SHIPPED -> nextStatus == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
