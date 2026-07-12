package com.jeongbeom.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCheckoutResponse {

    private String orderId;
    private String orderName;
    private int amount;
}
