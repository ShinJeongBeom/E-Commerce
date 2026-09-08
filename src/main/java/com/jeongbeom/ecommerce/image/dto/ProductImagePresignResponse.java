package com.jeongbeom.ecommerce.image.dto;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ProductImagePresignResponse {

    private final String uploadUrl; // S3 업로드 주소
    private final String objectKey; // 업로드시 확인할 식별자
    private final Instant expiresAt;// URL 만료 시각

    public ProductImagePresignResponse(String uploadUrl, String objectKey, Instant expiresAt) {
        this.uploadUrl = uploadUrl;
        this.objectKey = objectKey;
        this.expiresAt = expiresAt;
    }
}
