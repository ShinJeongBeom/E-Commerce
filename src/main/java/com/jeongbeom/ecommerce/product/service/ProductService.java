package com.jeongbeom.ecommerce.product.service;

import com.jeongbeom.ecommerce.product.dto.ProductCreateRequest;
import com.jeongbeom.ecommerce.product.dto.ProductResponse;
import com.jeongbeom.ecommerce.product.entity.Product;
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
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        return new ProductResponse(product);
    }

    //상품 목록 조회
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::new)
                .toList();
    }

}
