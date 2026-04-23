package com.jeongbeom.ecommerce.cart.controller;

import com.jeongbeom.ecommerce.cart.dto.AddCartRequestDto;
import com.jeongbeom.ecommerce.cart.dto.CartItemResponseDto;
import com.jeongbeom.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<String> addCartItem(Authentication authentication,
                                              @RequestBody AddCartRequestDto addCartRequestDto) {
        Long memberId = Long.parseLong(authentication.getName());
        cartService.addCartItem(memberId, addCartRequestDto);
        return ResponseEntity.ok( "장바구니 추가 완료");
    }

    /**
     * 장바구니 조회
     */
    @GetMapping
    public List<CartItemResponseDto> getCartItems(Authentication authentication) {
        Long memberId = Long.parseLong(authentication.getName());
        return cartService.getCartItems(memberId);
    }

    /**
     * 장바구니 상품 삭제
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> removeCartItem(Authentication authentication,
                               @PathVariable Long cartItemId) {
        Long memberId = Long.parseLong(authentication.getName());
        cartService.removeCartItem(memberId, cartItemId);

        return ResponseEntity.ok("장바구니 삭제 완료");
    }
}