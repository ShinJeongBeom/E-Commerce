package com.jeongbeom.ecommerce.image;

import lombok.Getter;

import java.time.Instant;

// 서비스와 AWS S3를 분리
public interface ProductImageStorage {

    PresignedUpload createPresignedUpload(String objectKey, String contentType, Instant expiresAt);

    UploadedObjectMetadata getMetadata(String objectKey);

    String getImageUrl(String objectKey);

    void delete(String objectKey);

    // PresignedUpload 생성결과를 저장
    @Getter
    class PresignedUpload {

        private final String uploadUrl;
        private final String objectKey;
        private final Instant expiresAt;

        public PresignedUpload(String uploadUrl, String objectKey, Instant expiresAt) {
            this.uploadUrl = uploadUrl;
            this.objectKey = objectKey;
            this.expiresAt = expiresAt;
        }
    }

    // S3에서 조회한 실제 파일 정보를 전달
    @Getter
    class UploadedObjectMetadata {

        private final long contentLength;
        private final String contentType;

        // 서비스 계층에 노출하지 않기위해 별도 클래스로 감쌈
        public UploadedObjectMetadata(long contentLength, String contentType) {
            this.contentLength = contentLength;
            this.contentType = contentType;
        }
    }
}
