package com.jeongbeom.ecommerce.image.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductImageCompleteRequest {

    private String objectKey;

    public ProductImageCompleteRequest(String objectKey) {
        this.objectKey = objectKey;
    }
}
