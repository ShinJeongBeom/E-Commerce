package com.jeongbeom.ecommerce.seller.service;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.seller.dto.AdminSellerResponse;
import com.jeongbeom.ecommerce.seller.entity.SellerApprovalStatus;
import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import com.jeongbeom.ecommerce.seller.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSellerService {

    private final SellerProfileRepository sellerProfileRepository;

    @Transactional(readOnly = true)
    public List<AdminSellerResponse> getSellers(SellerApprovalStatus status) {
        List<SellerProfile> sellerProfiles = status == null
                ? sellerProfileRepository.findAll()
                : sellerProfileRepository.findByApprovalStatus(status);

        return sellerProfiles.stream()
                .map(AdminSellerResponse::new)
                .toList();
    }

    @Transactional
    public AdminSellerResponse approveSeller(Long sellerProfileId) {
        SellerProfile sellerProfile = getSellerProfile(sellerProfileId);
        sellerProfile.approve();
        return new AdminSellerResponse(sellerProfile);
    }

    @Transactional
    public AdminSellerResponse suspendSeller(Long sellerProfileId) {
        SellerProfile sellerProfile = getSellerProfile(sellerProfileId);
        sellerProfile.suspend();
        return new AdminSellerResponse(sellerProfile);
    }

    private SellerProfile getSellerProfile(Long sellerProfileId) {
        return sellerProfileRepository.findById(sellerProfileId)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_PROFILE_NOT_FOUND));
    }
}
