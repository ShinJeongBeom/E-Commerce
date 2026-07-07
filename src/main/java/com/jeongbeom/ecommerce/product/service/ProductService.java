package com.jeongbeom.ecommerce.product.service;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
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
import com.jeongbeom.ecommerce.seller.entity.SellerApprovalStatus;
import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import com.jeongbeom.ecommerce.seller.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final SellerProfileRepository sellerProfileRepository;

    //상품 등록
    @Transactional
    public Long createProduct(Long memberId, ProductCreateRequest productCreateRequest) {
        Member member = getMember(memberId);
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

        if (member.getRole() == Role.SELLER) {
            product.assignSellerProfile(getApprovedSellerProfile(member));
        }

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
    public void updateProduct(Long memberId, Long productId, ProductUpdateRequest request) {
        Member member = getMember(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        validateProductManagePermission(member, product);

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
    public void deleteProduct(Long memberId, Long productId) {
        Member member = getMember(memberId);
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        validateProductManagePermission(member, product);

        product.hide();
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private SellerProfile getApprovedSellerProfile(Member member) {
        SellerProfile sellerProfile = sellerProfileRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_PROFILE_NOT_FOUND));

        if (sellerProfile.getApprovalStatus() != SellerApprovalStatus.APPROVED) {
            throw new CustomException(ErrorCode.SELLER_NOT_APPROVED);
        }

        return sellerProfile;
    }

    private void validateProductManagePermission(Member member, Product product) {
        if (member.getRole() == Role.ADMIN) {
            return;
        }

        if (member.getRole() != Role.SELLER) {
            throw new CustomException(ErrorCode.SELLER_ACCESS_DENIED);
        }

        SellerProfile sellerProfile = getApprovedSellerProfile(member);
        if (product.getSellerProfile() == null || !product.getSellerProfile().getId().equals(sellerProfile.getId())) {
            throw new CustomException(ErrorCode.PRODUCT_ACCESS_DENIED);
        }
    }


}
