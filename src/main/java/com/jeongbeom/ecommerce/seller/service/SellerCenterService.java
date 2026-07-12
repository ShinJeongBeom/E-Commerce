package com.jeongbeom.ecommerce.seller.service;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderItem;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.order.entity.repository.OrderItemRepository;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.seller.dto.*;
import com.jeongbeom.ecommerce.seller.entity.SellerApprovalStatus;
import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import com.jeongbeom.ecommerce.seller.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerCenterService {

    private final MemberRepository memberRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public SellerDashboardResponse getDashboard(Long memberId) {
        SellerProfile profile = getSellerProfile(memberId);
        if (!isApproved(profile)) {
            return createPendingDashboard(profile);
        }

        List<OrderItem> sellerOrderItems = getSellerOrderItems(profile);
        int totalSales = sellerOrderItems.stream()
                .filter(orderItem -> isPaidOrder(orderItem.getOrder()))
                .mapToInt(this::calculateOrderItemTotalPrice)
                .sum();

        List<Order> sellerOrders = getSellerOrders(sellerOrderItems);

        int cancelledCount = (int) sellerOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.CANCELLED)
                .count();

        int preparingCount = (int) sellerOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PREPARING || order.getStatus() == OrderStatus.PAID)
                .count();

        int waitingPaymentCount = (int) sellerOrders.stream()
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
                sellerOrders.size(),
                (int) productRepository.countBySellerProfile(profile),
                List.of("판매자 운영 정책 안내", "정산 기준 및 배송 정책 확인", "상품 이미지 등록 가이드")
        );
    }

    public SellerProfileResponse getProfile(Long memberId) {
        return new SellerProfileResponse(getSellerProfile(memberId));
    }

    public List<SellerProductResponse> getProducts(Long memberId) {
        SellerProfile profile = getSellerProfile(memberId);
        if (!isApproved(profile)) {
            return List.of();
        }

        return productRepository.findBySellerProfile(profile).stream()
                .map(SellerProductResponse::new)
                .toList();
    }

    public List<SellerOrderResponse> getOrders(Long memberId) {
        SellerProfile profile = getSellerProfile(memberId);
        if (!isApproved(profile)) {
            return List.of();
        }

        return getSellerOrderTotals(getSellerOrderItems(profile)).entrySet().stream()
                .map(entry -> new SellerOrderResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    public SellerSalesResponse getSales(Long memberId) {
        SellerProfile profile = getSellerProfile(memberId);
        if (!isApproved(profile)) {
            return new SellerSalesResponse(0, 0, 0, 0);
        }

        List<OrderItem> sellerOrderItems = getSellerOrderItems(profile);
        int totalSales = sellerOrderItems.stream()
                .filter(orderItem -> isPaidOrder(orderItem.getOrder()))
                .mapToInt(this::calculateOrderItemTotalPrice)
                .sum();
        int orderCount = (int) getSellerOrders(sellerOrderItems).stream()
                .filter(this::isPaidOrder)
                .count();
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

    private boolean isApproved(SellerProfile profile) {
        return profile.getApprovalStatus() == SellerApprovalStatus.APPROVED;
    }

    private SellerDashboardResponse createPendingDashboard(SellerProfile profile) {
        return new SellerDashboardResponse(
                new SellerProfileResponse(profile),
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                List.of("판매자 승인 검토가 진행 중입니다.", "승인 완료 후 상품과 주문 관리가 활성화됩니다.")
        );
    }

    private List<OrderItem> getSellerOrderItems(SellerProfile profile) {
        return orderItemRepository.findByProductSellerProfile(profile);
    }

    private List<Order> getSellerOrders(List<OrderItem> orderItems) {
        return getSellerOrderTotals(orderItems).keySet().stream().toList();
    }

    private Map<Order, Integer> getSellerOrderTotals(List<OrderItem> orderItems) {
        Map<Order, Integer> orderTotals = new LinkedHashMap<>();
        for (OrderItem orderItem : orderItems) {
            Order order = orderItem.getOrder();
            orderTotals.merge(order, calculateOrderItemTotalPrice(orderItem), Integer::sum);
        }
        return orderTotals;
    }

    private int calculateOrderItemTotalPrice(OrderItem orderItem) {
        return orderItem.getOrderPrice() * orderItem.getOrderQuantity();
    }

    private boolean isPaidOrder(Order order) {
        return order.getStatus() == OrderStatus.PAID
                || order.getStatus() == OrderStatus.PREPARING
                || order.getStatus() == OrderStatus.SHIPPED
                || order.getStatus() == OrderStatus.DELIVERED;
    }
}
