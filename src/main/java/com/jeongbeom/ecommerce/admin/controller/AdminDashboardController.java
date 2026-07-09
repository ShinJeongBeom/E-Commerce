package com.jeongbeom.ecommerce.admin.controller;

import com.jeongbeom.ecommerce.admin.dto.AdminDashboardResponse;
import com.jeongbeom.ecommerce.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public AdminDashboardResponse getDashboard(Authentication authentication) {
        return adminDashboardService.getDashboard(getMemberId(authentication));
    }

    private Long getMemberId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
