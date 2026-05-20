package com.jeongbeom.ecommerce.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CartOrderRequest {
    private List<Long> cartItemIds;

    private String name;
    private String phone;
    private String address;

    public CartOrderRequest(List<Long> cartItemIds, String name, String phone, String address) {
        this.cartItemIds = cartItemIds;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
}
