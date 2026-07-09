package com.jeongbeom.ecommerce.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerInquiryResponse {
    private Long id;
    private String title;
    private String status;
}
