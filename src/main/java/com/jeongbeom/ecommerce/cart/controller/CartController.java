package com.jeongbeom.ecommerce.cart.controller;

import com.jeongbeom.ecommerce.cart.dto.AddCartRequestDto;
import com.jeongbeom.ecommerce.cart.dto.CartItemResponseDto;
import com.jeongbeom.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 상품 추가
     */
    @PostMapping
    public ResponseEntity<String> addCartItem(@RequestBody AddCartRequestDto addCartRequestDto) {
        cartService.addCartItem(addCartRequestDto);

        return ResponseEntity.ok( "장바구니 추가 완료");
    }

    /**
     * 장바구니 조회
     */
    @GetMapping
    public List<CartItemResponseDto> getCartItems(@RequestParam Long memberId) {
        return cartService.getCartItems(memberId);
    }

    /**
     * 장바구니 상품 삭제
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> removeCartItem(@RequestParam Long memberId,
                               @PathVariable Long cartItemId) {
        cartService.removeCartItem(memberId, cartItemId);

        return ResponseEntity.ok("장바구니 삭제 완료");
    }
}