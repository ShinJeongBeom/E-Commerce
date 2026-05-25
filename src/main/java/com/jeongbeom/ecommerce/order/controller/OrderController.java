package com.jeongbeom.ecommerce.order.controller;

import com.jeongbeom.ecommerce.order.dto.CartOrderRequest;
import com.jeongbeom.ecommerce.order.dto.OrderCreateRequestDto;
import com.jeongbeom.ecommerce.order.dto.OrderResponseDto;
import com.jeongbeom.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<String> createOrder(Authentication authentication,
                                              @RequestBody OrderCreateRequestDto requestDto) {
        Long memberId = Long.parseLong(authentication.getName());
        orderService.createOrder(memberId,requestDto);
        return ResponseEntity.ok("주문이 완료되었습니다.");
    }

    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(Authentication authentication,
                                              @PathVariable Long orderId) {
       Long memberId = Long.parseLong(authentication.getName());
       orderService.cancelOrder(memberId, orderId);
        return ResponseEntity.ok("주문이 취소되었습니다.");
    }

    // 주문 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByMember(Authentication authentication) {
        Long memberId =  Long.parseLong(authentication.getName());
        return ResponseEntity.ok(orderService.getOrdersByMemberId(memberId));
    }

    // 장바구니 전체 주문
    @PostMapping("/cart")
    public ResponseEntity<String> createOrderFromCart(
            Authentication authentication,
            @RequestBody CartOrderRequest request
    ) {
        Long memberId = Long.parseLong(authentication.getName());
        orderService.createOrderFromCart(memberId, request);

        return ResponseEntity.ok("장바구니 전체 주문이 완료되었습니다.");
    }

    // 장바구니 선택 주문
    @PostMapping("/cart/items")
    public ResponseEntity<String> createOrderFromCartItems(
            Authentication authentication,
            @RequestBody CartOrderRequest request
    ) {
        Long memberId = Long.parseLong(authentication.getName());
        orderService.createOrderFromCartItems(memberId, request);

        return ResponseEntity.ok("선택 장바구니 상품 주문이 완료되었습니다.");
    }
}