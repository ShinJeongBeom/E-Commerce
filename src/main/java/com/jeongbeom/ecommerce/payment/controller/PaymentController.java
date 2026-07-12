package com.jeongbeom.ecommerce.payment.controller;

import com.jeongbeom.ecommerce.payment.dto.PaymentConfirmRequest;
import com.jeongbeom.ecommerce.payment.dto.PaymentResponse;
import com.jeongbeom.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            Authentication authentication,
            @RequestBody PaymentConfirmRequest request
    ) {
        Long memberId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(paymentService.confirmPayment(memberId, request));
    }
}
