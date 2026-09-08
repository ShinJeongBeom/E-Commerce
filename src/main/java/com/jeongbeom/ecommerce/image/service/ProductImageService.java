package com.jeongbeom.ecommerce.image.service;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.image.*;
import com.jeongbeom.ecommerce.image.dto.ProductImageCompleteRequest;
import com.jeongbeom.ecommerce.image.dto.ProductImagePresignRequest;
import com.jeongbeom.ecommerce.image.dto.ProductImagePresignResponse;
import com.jeongbeom.ecommerce.image.dto.ProductImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    //업로드 이미지 크기 제한 설정 5MB
    static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    // Presigned URL 유효시간 10분
    private static final Duration UPLOAD_URL_TTL = Duration.ofMinutes(10);

    // 허용 MIME 타입과 저장 확장자 연결
    private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private static final Set<String> ALLOWED_CONTENT_TYPES = EXTENSIONS_BY_CONTENT_TYPE.keySet();

    private final ProductImageStorage imageStorage;
    private final Clock clock = Clock.systemUTC();

    public ProductImagePresignResponse createUploadUrl(Long memberId, ProductImagePresignRequest request) {
        validatePresignRequest(request);

        String extension = EXTENSIONS_BY_CONTENT_TYPE.get(request.getContentType());

        // 회원 아이디 경로 포함 및 UUID로 파일명 충돌 방지를 위한 객체 키 생성
        String objectKey = "products/" + memberId + "/" + UUID.randomUUID() + extension;
        Instant expiresAt = clock.instant().plus(UPLOAD_URL_TTL);
        ProductImageStorage.PresignedUpload upload =
                imageStorage.createPresignedUpload(objectKey, request.getContentType(), expiresAt);

        return new ProductImagePresignResponse(upload.getUploadUrl(), upload.getObjectKey(), upload.getExpiresAt());
    }

    // 업로드시 값을 다시 검사함
    public ProductImageUploadResponse completeUpload(Long memberId, ProductImageCompleteRequest request) {
        String objectKey = request == null ? null : request.getObjectKey();
        validateOwnedObjectKey(memberId, objectKey);

        ProductImageStorage.UploadedObjectMetadata metadata = imageStorage.getMetadata(objectKey);
        if (metadata.getContentLength() <= 0
                || metadata.getContentLength() > MAX_FILE_SIZE
                || !ALLOWED_CONTENT_TYPES.contains(metadata.getContentType())) {
            imageStorage.delete(objectKey);
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }

        return new ProductImageUploadResponse(imageStorage.getImageUrl(objectKey), objectKey);
    }
    // 요청 객체가 없거나, 지원하지 않는 MIME타입, 파일크키가 0이하이거나 5MB 초과할시 거부
    private void validatePresignRequest(ProductImagePresignRequest request) {
        if (request == null
                || !ALLOWED_CONTENT_TYPES.contains(request.getContentType())
                || request.getFileSize() <= 0
                || request.getFileSize() > MAX_FILE_SIZE) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }
    }

    private void validateOwnedObjectKey(Long memberId, String objectKey) {
        String expectedPrefix = "products/" + memberId + "/";
        if (objectKey == null || !objectKey.startsWith(expectedPrefix) || objectKey.contains("..")) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_FILE);
        }
    }
}
