package com.jeongbeom.ecommerce.admin.service;

import com.jeongbeom.ecommerce.admin.dto.AdminDashboardResponse;
import com.jeongbeom.ecommerce.admin.entity.AdminInquiryStatus;
import com.jeongbeom.ecommerce.admin.entity.AdminReportStatus;
import com.jeongbeom.ecommerce.admin.entity.AdminReportTargetType;
import com.jeongbeom.ecommerce.admin.entity.AdminSettlementStatus;
import com.jeongbeom.ecommerce.admin.repository.AdminInquiryRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminReportRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminSettlementRepository;
import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.entity.MemberStatus;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.seller.entity.SellerApprovalStatus;
import com.jeongbeom.ecommerce.seller.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private static final LocalDate DEFAULT_DOMAIN_EXPIRES_AT = LocalDate.of(2026, 12, 31);

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final AdminInquiryRepository inquiryRepository;
    private final AdminReportRepository reportRepository;
    private final AdminSettlementRepository settlementRepository;

    public AdminDashboardResponse getDashboard(Long memberId) {
        Member admin = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        LocalDate today = LocalDate.now();
        LocalDateTime startDateTime = today.atStartOfDay();
        LocalDateTime endDateTime = today.plusDays(1).atStartOfDay();

        AdminDashboardResponse.TodayStatusResponse todayStatus =
                new AdminDashboardResponse.TodayStatusResponse(
                        memberRepository.countByCreatedAtBetween(startDateTime, endDateTime),
                        memberRepository.countByStatusAndUpdatedAtBetween(MemberStatus.DELETED, startDateTime, endDateTime),
                        productRepository.countByCreatedAtBetween(startDateTime, endDateTime),
                        orderRepository.countByCreatedAtBetween(startDateTime, endDateTime)
                );

        long sellerApprovalWaitingCount = sellerProfileRepository.countByApprovalStatus(SellerApprovalStatus.PENDING);
        long pendingProductReportCount =
                reportRepository.countByTargetTypeAndStatus(AdminReportTargetType.PRODUCT, AdminReportStatus.PENDING);
        long pendingReviewReportCount =
                reportRepository.countByTargetTypeAndStatus(AdminReportTargetType.REVIEW, AdminReportStatus.PENDING);

        AdminDashboardResponse.PendingStatusResponse pendingStatus =
                new AdminDashboardResponse.PendingStatusResponse(
                        pendingProductReportCount,
                        0,
                        inquiryRepository.countByStatus(AdminInquiryStatus.WAITING),
                        0,
                        sellerApprovalWaitingCount,
                        orderRepository.countByStatusIn(List.of(OrderStatus.CREATED, OrderStatus.PAID, OrderStatus.PREPARING))
                );

        AdminDashboardResponse.MarketplaceStatusResponse marketplaceStatus =
                new AdminDashboardResponse.MarketplaceStatusResponse(
                        sellerApprovalWaitingCount,
                        sellerProfileRepository.countByCreatedAtBetween(startDateTime, endDateTime),
                        settlementRepository.sumAmountByStatus(AdminSettlementStatus.PENDING),
                        pendingProductReportCount,
                        pendingReviewReportCount,
                        productRepository.countByStatus(ProductStatus.HIDDEN)
                );

        return new AdminDashboardResponse(
                admin.getLoginId(),
                today.toString(),
                DEFAULT_DOMAIN_EXPIRES_AT.toString(),
                ChronoUnit.DAYS.between(today, DEFAULT_DOMAIN_EXPIRES_AT),
                createQuickMenus(),
                todayStatus,
                pendingStatus,
                marketplaceStatus,
                createImprovementPosts(),
                createManualPosts()
        );
    }

    private List<AdminDashboardResponse.QuickMenuResponse> createQuickMenus() {
        return List.of(
                new AdminDashboardResponse.QuickMenuResponse("정책관리", "policy"),
                new AdminDashboardResponse.QuickMenuResponse("통계", "statistics"),
                new AdminDashboardResponse.QuickMenuResponse("상품등록", "products")
        );
    }

    private List<AdminDashboardResponse.BoardPostResponse> createImprovementPosts() {
        return List.of(
                new AdminDashboardResponse.BoardPostResponse("게시판 에디터 변경 요청", "admin02", "2026-07-01"),
                new AdminDashboardResponse.BoardPostResponse("회원정보 수정 오류", "admin03", "2026-07-01"),
                new AdminDashboardResponse.BoardPostResponse("결제 지연 수정 요청", "admin04", "2026-07-01"),
                new AdminDashboardResponse.BoardPostResponse("배송사 추가 요청", "admin05", "2026-07-01")
        );
    }

    private List<AdminDashboardResponse.BoardPostResponse> createManualPosts() {
        return List.of(
                new AdminDashboardResponse.BoardPostResponse("상품 교환 처리 프로세스", "admin02", "2026-07-01"),
                new AdminDashboardResponse.BoardPostResponse("1:1 문의 처리 방법", "admin03", "2026-07-01"),
                new AdminDashboardResponse.BoardPostResponse("사용자 결제 실패 시 조치", "admin04", "2026-07-01"),
                new AdminDashboardResponse.BoardPostResponse("배송사 추가 방법", "admin05", "2026-07-01")
        );
    }
}
