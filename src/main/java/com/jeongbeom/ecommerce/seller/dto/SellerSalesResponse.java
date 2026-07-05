package com.jeongbeom.ecommerce.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerSalesResponse {
    private int todaySalesAmount;
    private int monthlySalesAmount;
    private int orderCount;
    private int averageOrderAmount;
}
