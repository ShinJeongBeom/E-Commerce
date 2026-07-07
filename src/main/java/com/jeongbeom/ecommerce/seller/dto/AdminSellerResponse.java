package com.jeongbeom.ecommerce.seller.dto;

import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import lombok.Getter;

@Getter
public class AdminSellerResponse {
    private final Long id;
    private final Long memberId;
    private final String loginId;
    private final String email;
    private final String storeName;
    private final String approvalStatus;

    public AdminSellerResponse(SellerProfile sellerProfile) {
        this.id = sellerProfile.getId();
        this.memberId = sellerProfile.getMember().getId();
        this.loginId = sellerProfile.getMember().getLoginId();
        this.email = sellerProfile.getMember().getEmail();
        this.storeName = sellerProfile.getStoreName();
        this.approvalStatus = sellerProfile.getApprovalStatus().name();
    }
}
