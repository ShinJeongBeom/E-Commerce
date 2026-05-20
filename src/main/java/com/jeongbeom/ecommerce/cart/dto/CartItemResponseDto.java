package com.jeongbeom.ecommerce.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemResponseDto {

    private Long cartItemId;
    private Long productId;
    private String productName;
    private int price;
    private int quantity;

}
