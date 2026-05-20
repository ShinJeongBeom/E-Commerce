package com.jeongbeom.ecommerce.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemQuantityUpdateRequest {

    private int quantity;
}
