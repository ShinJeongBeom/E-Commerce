package com.jeongbeom.ecommerce.seller.service;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.seller.dto.*;
import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import com.jeongbeom.ecommerce.seller.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerCenterService {

    private final MemberRepository memberRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public SellerDashboardResponse getDashboard(Long memberId) {
        SellerProfile profile = getSellerProfile(memberId);
        List<Order> orders = orderRepository.findAll();
        int totalSales = orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .mapToInt(Order::getTotalPrice)
                .sum();

        int cancelledCount = (int) orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.CANCELLED)
                .count();

        int preparingCount = (int) orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PREPARING || order.getStatus() == OrderStatus.PAID)
                .count();

        int waitingPaymentCount = (int) orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.CREATED)
                .count();

        return new SellerDashboardResponse(
                new SellerProfileResponse(profile),
                waitingPaymentCount,
                preparingCount,
                cancelledCount,
                0,
                totalSales,
                totalSales,
                orders.size(),
                (int) productRepository.count(),
                List.of("판매자 운영 정책 안내", "정산 기준 및 배송 정책 확인", "상품 이미지 등록 가이드")
        );
    }

    public SellerProfileResponse getProfile(Long memberId) {
        return new SellerProfileResponse(getSellerProfile(memberId));
    }

    public List<SellerProductResponse> getProducts(Long memberId) {
        getSellerProfile(memberId);
        return productRepository.findAll().stream()
                .map(SellerProductResponse::new)
                .toList();
    }

    public List<SellerOrderResponse> getOrders(Long memberId) {
        getSellerProfile(memberId);
        return orderRepository.findAll().stream()
                .map(SellerOrderResponse::new)
                .toList();
    }

    public SellerSalesResponse getSales(Long memberId) {
        getSellerProfile(memberId);
        List<Order> orders = orderRepository.findAll();
        int totalSales = orders.stream()
                .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
                .mapToInt(Order::getTotalPrice)
                .sum();
        int orderCount = orders.size();
        int averageOrderAmount = orderCount == 0 ? 0 : totalSales / orderCount;

        return new SellerSalesResponse(totalSales, totalSales, orderCount, averageOrderAmount);
    }

    public List<SellerInquiryResponse> getInquiries(Long memberId) {
        getSellerProfile(memberId);
        return List.of();
    }

    private SellerProfile getSellerProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (member.getRole() != Role.SELLER && member.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.SELLER_ACCESS_DENIED);
        }

        return sellerProfileRepository.findByMember(member)
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_PROFILE_NOT_FOUND));
    }
}
