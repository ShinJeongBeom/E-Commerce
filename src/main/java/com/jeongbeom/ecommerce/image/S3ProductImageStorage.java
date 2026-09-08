package com.jeongbeom.ecommerce.image;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class S3ProductImageStorage implements ProductImageStorage {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket:local-ecommerce-bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.public-base-url:}")
    private String publicBaseUrl;

    @Override
    public PresignedUpload createPresignedUpload(String objectKey, String contentType, Instant expiresAt) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.between(Instant.now(), expiresAt))
                .putObjectRequest(putObjectRequest)
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return new PresignedUpload(presignedRequest.url().toString(), objectKey, expiresAt);
        } catch (SdkException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL_EXCEPTION);
        }
    }

    @Override
    public UploadedObjectMetadata getMetadata(String objectKey) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        try {
            HeadObjectResponse metadata = s3Client.headObject(request);
            return new UploadedObjectMetadata(metadata.contentLength(), metadata.contentType());
        } catch (SdkException e) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_NOT_FOUND);
        }
    }

    @Override
    public String getImageUrl(String objectKey) {
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            return publicBaseUrl.replaceAll("/+$", "") + "/" + objectKey;
        }

        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        return s3Client.utilities().getUrl(request).toString();
    }

    @Override
    public void delete(String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        try {
            s3Client.deleteObject(request);
        } catch (SdkException ignored) {
        }
    }
}
