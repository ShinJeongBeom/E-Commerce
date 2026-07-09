package com.jeongbeom.ecommerce.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SellerDashboardResponse {
    private SellerProfileResponse profile;
    private int waitingPaymentCount;
    private int preparingDeliveryCount;
    private int cancelledOrderCount;
    private int todaySettlementAmount;
    private int todaySalesAmount;
    private int monthlySalesAmount;
    private int orderCount;
    private int productCount;
    private List<String> notices;
}
