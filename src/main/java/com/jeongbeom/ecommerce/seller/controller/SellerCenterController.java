package com.jeongbeom.ecommerce.seller.controller;

import com.jeongbeom.ecommerce.seller.dto.*;
import com.jeongbeom.ecommerce.seller.service.SellerCenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller-center")
public class SellerCenterController {

    private final SellerCenterService sellerCenterService;

    @GetMapping("/profile")
    public SellerProfileResponse getProfile(Authentication authentication) {
        return sellerCenterService.getProfile(getMemberId(authentication));
    }

    @GetMapping("/dashboard")
    public SellerDashboardResponse getDashboard(Authentication authentication) {
        return sellerCenterService.getDashboard(getMemberId(authentication));
    }

    @GetMapping("/products")
    public List<SellerProductResponse> getProducts(Authentication authentication) {
        return sellerCenterService.getProducts(getMemberId(authentication));
    }

    @GetMapping("/orders")
    public List<SellerOrderResponse> getOrders(Authentication authentication) {
        return sellerCenterService.getOrders(getMemberId(authentication));
    }

    @GetMapping("/sales")
    public SellerSalesResponse getSales(Authentication authentication) {
        return sellerCenterService.getSales(getMemberId(authentication));
    }

    @GetMapping("/inquiries")
    public List<SellerInquiryResponse> getInquiries(Authentication authentication) {
        return sellerCenterService.getInquiries(getMemberId(authentication));
    }

    private Long getMemberId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
