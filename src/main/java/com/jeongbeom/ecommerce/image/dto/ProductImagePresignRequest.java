package com.jeongbeom.ecommerce.image.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor //JSON을 JAVA 객체로 변환
public class ProductImagePresignRequest {

    private String contentType;
    private long fileSize;

    public ProductImagePresignRequest(String contentType, long fileSize) {
        this.contentType = contentType;
        this.fileSize = fileSize;
    }
}
