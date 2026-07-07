package com.jeongbeom.ecommerce.seller.controller;

import com.jeongbeom.ecommerce.seller.dto.AdminSellerResponse;
import com.jeongbeom.ecommerce.seller.entity.SellerApprovalStatus;
import com.jeongbeom.ecommerce.seller.service.AdminSellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/sellers")
public class AdminSellerController {

    private final AdminSellerService adminSellerService;

    @GetMapping
    public List<AdminSellerResponse> getSellers(
            @RequestParam(required = false) SellerApprovalStatus status
    ) {
        return adminSellerService.getSellers(status);
    }

    @PatchMapping("/{sellerProfileId}/approve")
    public AdminSellerResponse approveSeller(@PathVariable Long sellerProfileId) {
        return adminSellerService.approveSeller(sellerProfileId);
    }

    @PatchMapping("/{sellerProfileId}/suspend")
    public AdminSellerResponse suspendSeller(@PathVariable Long sellerProfileId) {
        return adminSellerService.suspendSeller(sellerProfileId);
    }
}
