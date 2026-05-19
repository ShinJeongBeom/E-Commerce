package com.jeongbeom.ecommerce.product.service;

import com.jeongbeom.ecommerce.product.dto.ProductCreateRequest;
import com.jeongbeom.ecommerce.product.dto.ProductResponse;
import com.jeongbeom.ecommerce.product.dto.ProductUpdateRequest;
import com.jeongbeom.ecommerce.product.entity.CareLevel;
import com.jeongbeom.ecommerce.product.entity.LightRequirement;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.WateringCycle;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    //상품 등록
    @Transactional
    public Long createProduct(ProductCreateRequest productCreateRequest) {
        Product product = new Product(
                productCreateRequest.getName(),
                productCreateRequest.getPlantType(),
                productCreateRequest.getCareLevel(),
                productCreateRequest.getLightRequirement(),
                productCreateRequest.getWateringCycle(),
                productCreateRequest.getImageUrl(),
                productCreateRequest.getPotIncluded(),
                productCreateRequest.getDescription(),
                productCreateRequest.getPrice(),
                productCreateRequest.getStock(),
                productCreateRequest.getStatus()
        );

        return productRepository.save(product).getId();
    }

    //상품 조회
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findByIdAndStatusNot(productId, ProductStatus.HIDDEN)
                .orElseThrow(ProductNotFoundException::new);

        return new ProductResponse(product);
    }

    // 상품 목록 조회 필터 값이 있으면 조건 조회, 없으면 전체 조회
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(
            CareLevel careLevel,
            LightRequirement lightRequirement,
            WateringCycle wateringCycle
    ) {
        return productRepository.findProductsByFilters(
                        careLevel,
                        lightRequirement,
                        wateringCycle,
                        ProductStatus.HIDDEN
                ).stream()
                .map(ProductResponse::new)
                .toList();
    }

    // 상품 수정
    @Transactional
    public void updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        product.update(
                request.getName(),
                request.getPlantType(),
                request.getCareLevel(),
                request.getLightRequirement(),
                request.getWateringCycle(),
                request.getImageUrl(),
                request.getPotIncluded(),
                request.getDescription(),
                request.getPrice(),
                request.getStock(),
                request.getStatus()
        );
    }

    //상품 삭제 ( 실제 삭제 x -> Hidden 으로 상태 변경)
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        product.hide();
    }


}
