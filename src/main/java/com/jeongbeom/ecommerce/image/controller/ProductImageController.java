package com.jeongbeom.ecommerce.image.controller;

import com.jeongbeom.ecommerce.image.dto.ProductImageCompleteRequest;
import com.jeongbeom.ecommerce.image.dto.ProductImagePresignRequest;
import com.jeongbeom.ecommerce.image.dto.ProductImagePresignResponse;
import com.jeongbeom.ecommerce.image.dto.ProductImageUploadResponse;
import com.jeongbeom.ecommerce.image.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/images")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("/presigned")
    public ResponseEntity<ProductImagePresignResponse> createUploadUrl(
            Authentication authentication,
            @RequestBody ProductImagePresignRequest request
    ) {
        Long memberId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(productImageService.createUploadUrl(memberId, request));
    }

    @PostMapping("/complete")
    public ResponseEntity<ProductImageUploadResponse> completeUpload(
            Authentication authentication,
            @RequestBody ProductImageCompleteRequest request
    ) {
        Long memberId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(productImageService.completeUpload(memberId, request));
    }
}
