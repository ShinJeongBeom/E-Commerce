package com.jeongbeom.ecommerce.seller.dto;

import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import lombok.Getter;

@Getter
public class SellerProfileResponse {
    private final Long sellerProfileId;
    private final String storeName;
    private final String approvalStatus;

    public SellerProfileResponse(SellerProfile sellerProfile) {
        this.sellerProfileId = sellerProfile.getId();
        this.storeName = sellerProfile.getStoreName();
        this.approvalStatus = sellerProfile.getApprovalStatus().name();
    }
}
