package com.jeongbeom.ecommerce.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequestDto {

    private Long memberId;
    private Long productId;
    private int quantity;

    private String name;
    private String phone;
    private String address;

    public OrderCreateRequestDto(Long memberId, Long productId, int quantity, String name, String phone, String address) {
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
}
