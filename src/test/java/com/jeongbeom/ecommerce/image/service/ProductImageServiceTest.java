package com.jeongbeom.ecommerce.image.service;

import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.image.ProductImageStorage;
import com.jeongbeom.ecommerce.image.dto.ProductImageCompleteRequest;
import com.jeongbeom.ecommerce.image.dto.ProductImagePresignRequest;
import com.jeongbeom.ecommerce.image.dto.ProductImagePresignResponse;
import com.jeongbeom.ecommerce.image.dto.ProductImageUploadResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductImageStorage imageStorage;

    @Test
    void createsPresignedUploadForOwnedProductPath() {
        ProductImageService service = new ProductImageService(imageStorage);
        when(imageStorage.createPresignedUpload(anyString(), eq("image/png"), any(Instant.class)))
                .thenAnswer(invocation -> new ProductImageStorage.PresignedUpload(
                        "https://s3.example.com/upload",
                        invocation.getArgument(0),
                        invocation.getArgument(2)
                ));

        ProductImagePresignResponse response = service.createUploadUrl(
                7L,
                new ProductImagePresignRequest("image/png", 1024)
        );

        assertThat(response.getUploadUrl()).isEqualTo("https://s3.example.com/upload");
        assertThat(response.getObjectKey()).startsWith("products/7/").endsWith(".png");
    }

    @Test
    void rejectsOversizedFileBeforeCreatingPresignedUrl() {
        ProductImageService service = new ProductImageService(imageStorage);

        assertThatThrownBy(() -> service.createUploadUrl(
                7L,
                new ProductImagePresignRequest("image/jpeg", ProductImageService.MAX_FILE_SIZE + 1)
        )).isInstanceOf(CustomException.class);

        verifyNoInteractions(imageStorage);
    }

    @Test
    void verifiesUploadedObjectBeforeReturningImageUrl() {
        ProductImageService service = new ProductImageService(imageStorage);
        String objectKey = "products/7/image.webp";
        when(imageStorage.getMetadata(objectKey))
                .thenReturn(new ProductImageStorage.UploadedObjectMetadata(2048, "image/webp"));
        when(imageStorage.getImageUrl(objectKey)).thenReturn("https://cdn.example.com/" + objectKey);

        ProductImageUploadResponse response = service.completeUpload(
                7L,
                new ProductImageCompleteRequest(objectKey)
        );

        assertThat(response.getImageUrl()).isEqualTo("https://cdn.example.com/" + objectKey);
        assertThat(response.getObjectKey()).isEqualTo(objectKey);
        verify(imageStorage, never()).delete(anyString());
    }

    @Test
    void deletesObjectWhenUploadedMetadataIsInvalid() {
        ProductImageService service = new ProductImageService(imageStorage);
        String objectKey = "products/7/image.jpg";
        when(imageStorage.getMetadata(objectKey))
                .thenReturn(new ProductImageStorage.UploadedObjectMetadata(
                        ProductImageService.MAX_FILE_SIZE + 1,
                        "image/jpeg"
                ));

        assertThatThrownBy(() -> service.completeUpload(
                7L,
                new ProductImageCompleteRequest(objectKey)
        )).isInstanceOf(CustomException.class);

        verify(imageStorage).delete(objectKey);
    }

    @Test
    void rejectsCompletingAnotherMembersObject() {
        ProductImageService service = new ProductImageService(imageStorage);

        assertThatThrownBy(() -> service.completeUpload(
                7L,
                new ProductImageCompleteRequest("products/8/image.jpg")
        )).isInstanceOf(CustomException.class);

        verifyNoInteractions(imageStorage);
    }
}
