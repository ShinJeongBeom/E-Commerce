package com.jeongbeom.ecommerce.image.dto;

import lombok.Getter;

@Getter
public class ProductImageUploadResponse {

    private final String imageUrl;
    private final String objectKey;

    public ProductImageUploadResponse(String imageUrl, String objectKey) {
        this.imageUrl = imageUrl;
        this.objectKey = objectKey;
    }
}
