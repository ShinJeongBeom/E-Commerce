package com.jeongbeom.ecommerce.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddCartRequestDto {

    @NotNull
    private Long productId; // 어떤 상품을 담는지

    @Min(1) // 수량 0 방지.
    private int quantity;   // 몇개를 담는지
}
