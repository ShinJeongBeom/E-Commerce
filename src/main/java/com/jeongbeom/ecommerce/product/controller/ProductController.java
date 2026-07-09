package com.jeongbeom.ecommerce.product.controller;

import com.jeongbeom.ecommerce.product.dto.ProductCreateRequest;
import com.jeongbeom.ecommerce.product.dto.ProductResponse;
import com.jeongbeom.ecommerce.product.dto.ProductUpdateRequest;
import com.jeongbeom.ecommerce.product.entity.CareLevel;
import com.jeongbeom.ecommerce.product.entity.LightRequirement;
import com.jeongbeom.ecommerce.product.entity.WateringCycle;
import com.jeongbeom.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Long> createProduct(
            Authentication authentication,
            @RequestBody ProductCreateRequest request
    ) {
        Long productId = productService.createProduct(getMemberId(authentication), request);
        return ResponseEntity.ok(productId);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    // 상품 정보 수정
    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(
            @PathVariable Long productId,
            Authentication authentication,
            @RequestBody ProductUpdateRequest request
    ) {
        productService.updateProduct(getMemberId(authentication), productId, request);
        return ResponseEntity.ok().build();
    }

    // 상품 정보 조회
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) CareLevel careLevel,
            @RequestParam(required = false) LightRequirement lightRequirement,
            @RequestParam(required = false) WateringCycle wateringCycle
    ) {
        return ResponseEntity.ok(productService.getProducts(careLevel, lightRequirement, wateringCycle));
    }

    // 특정 상품 삭제(상태변경)
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        productService.deleteProduct(getMemberId(authentication), productId);
        return ResponseEntity.ok().build();
    }

    private Long getMemberId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
