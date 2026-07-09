package com.jeongbeom.ecommerce.admin.service;

import com.jeongbeom.ecommerce.admin.dto.AdminManagementRequest;
import com.jeongbeom.ecommerce.admin.dto.AdminManagementResponse;
import com.jeongbeom.ecommerce.admin.entity.AdminAuditLog;
import com.jeongbeom.ecommerce.admin.entity.AdminBanner;
import com.jeongbeom.ecommerce.admin.entity.AdminInquiry;
import com.jeongbeom.ecommerce.admin.entity.AdminInquiryStatus;
import com.jeongbeom.ecommerce.admin.entity.AdminPolicy;
import com.jeongbeom.ecommerce.admin.entity.AdminPost;
import com.jeongbeom.ecommerce.admin.entity.AdminPostType;
import com.jeongbeom.ecommerce.admin.entity.AdminReport;
import com.jeongbeom.ecommerce.admin.entity.AdminReportStatus;
import com.jeongbeom.ecommerce.admin.entity.AdminReportTargetType;
import com.jeongbeom.ecommerce.admin.entity.AdminSettlement;
import com.jeongbeom.ecommerce.admin.entity.AdminSettlementStatus;
import com.jeongbeom.ecommerce.admin.repository.AdminAuditLogRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminBannerRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminInquiryRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminPolicyRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminPostRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminReportRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminSettlementRepository;
import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.entity.MemberStatus;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.dto.OrderResponseDto;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.product.dto.ProductResponse;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.product.exception.ProductNotFoundException;
import com.jeongbeom.ecommerce.seller.entity.SellerApprovalStatus;
import com.jeongbeom.ecommerce.seller.entity.SellerProfile;
import com.jeongbeom.ecommerce.seller.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final AdminAuditLogRepository auditLogRepository;
    private final AdminBannerRepository bannerRepository;
    private final AdminPolicyRepository policyRepository;
    private final AdminPostRepository postRepository;
    private final AdminInquiryRepository inquiryRepository;
    private final AdminReportRepository reportRepository;
    private final AdminSettlementRepository settlementRepository;

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<AdminManagementResponse.MemberResponse> getMembers(
            String keyword,
            String role,
            MemberStatus status,
            int page,
            int size
    ) {
        List<AdminManagementResponse.MemberResponse> items = memberRepository.findAll().stream()
                .filter(member -> matchesKeyword(keyword, member.getLoginId(), member.getEmail(), member.getPhone()))
                .filter(member -> isBlank(role) || member.getRole().name().equals(role))
                .filter(member -> status == null || member.getStatus() == status)
                .sorted(Comparator.comparing(Member::getId).reversed())
                .map(AdminManagementResponse.MemberResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<OrderResponseDto> getOrders(
            String keyword,
            OrderStatus status,
            int page,
            int size
    ) {
        List<OrderResponseDto> items = orderRepository.findAll().stream()
                .filter(order -> matchesKeyword(keyword, order.getOrderNumber(), order.getName(), order.getPhone(), order.getAddress()))
                .filter(order -> status == null || order.getStatus() == status)
                .sorted(Comparator.comparing(Order::getId).reversed())
                .map(OrderResponseDto::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional
    public AdminManagementResponse.MemberResponse suspendMember(Long adminMemberId, Long memberId) {
        Member member = getMember(memberId);
        member.suspend();
        audit(adminMemberId, "SUSPEND", "MEMBER", memberId, "회원 정지: " + member.getLoginId());
        return new AdminManagementResponse.MemberResponse(member);
    }

    @Transactional
    public AdminManagementResponse.MemberResponse deleteMember(Long adminMemberId, Long memberId) {
        Member member = getMember(memberId);
        member.deleteAccount();
        audit(adminMemberId, "DELETE", "MEMBER", memberId, "회원 탈퇴 상태 처리: " + member.getLoginId());
        return new AdminManagementResponse.MemberResponse(member);
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<ProductResponse> getProducts(
            String keyword,
            ProductStatus status,
            int page,
            int size
    ) {
        List<ProductResponse> items = productRepository.findAll().stream()
                .filter(product -> matchesKeyword(keyword, product.getName(), product.getPlantType(), product.getDescription()))
                .filter(product -> status == null || product.getStatus() == status)
                .sorted(Comparator.comparing(Product::getId).reversed())
                .map(ProductResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional
    public ProductResponse hideProduct(Long adminMemberId, Long productId) {
        Product product = getProduct(productId);
        product.hide();
        audit(adminMemberId, "HIDE", "PRODUCT", productId, "상품 숨김: " + product.getName());
        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse restoreProduct(Long adminMemberId, Long productId) {
        Product product = getProduct(productId);
        product.restore();
        audit(adminMemberId, "RESTORE", "PRODUCT", productId, "상품 복구: " + product.getName());
        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse deleteProduct(Long adminMemberId, Long productId) {
        Product product = getProduct(productId);
        product.hide();
        audit(adminMemberId, "ARCHIVE", "PRODUCT", productId, "상품 삭제 요청을 숨김 처리로 보관: " + product.getName());
        return new ProductResponse(product);
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<AdminManagementResponse.BannerResponse> getBanners(
            String keyword,
            Boolean visible,
            int page,
            int size
    ) {
        List<AdminManagementResponse.BannerResponse> items = bannerRepository.findAllByOrderBySortOrderAscIdDesc().stream()
                .filter(banner -> matchesKeyword(keyword, banner.getTitle(), banner.getLinkUrl()))
                .filter(banner -> visible == null || banner.isVisible() == visible)
                .map(AdminManagementResponse.BannerResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional
    public AdminManagementResponse.BannerResponse createBanner(Long adminMemberId, AdminManagementRequest.BannerUpsertRequest request) {
        AdminBanner banner = new AdminBanner(
                request.getTitle(),
                request.getImageUrl(),
                request.getLinkUrl(),
                request.isVisible(),
                request.getSortOrder()
        );
        AdminBanner savedBanner = bannerRepository.save(banner);
        audit(adminMemberId, "CREATE", "BANNER", savedBanner.getId(), "배너 등록: " + savedBanner.getTitle());
        return new AdminManagementResponse.BannerResponse(savedBanner);
    }

    @Transactional
    public AdminManagementResponse.BannerResponse updateBanner(Long adminMemberId, Long bannerId, AdminManagementRequest.BannerUpsertRequest request) {
        AdminBanner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        banner.update(request.getTitle(), request.getImageUrl(), request.getLinkUrl(), request.isVisible(), request.getSortOrder());
        audit(adminMemberId, "UPDATE", "BANNER", bannerId, "배너 수정: " + banner.getTitle());
        return new AdminManagementResponse.BannerResponse(banner);
    }

    @Transactional
    public void deleteBanner(Long adminMemberId, Long bannerId) {
        bannerRepository.deleteById(bannerId);
        audit(adminMemberId, "DELETE", "BANNER", bannerId, "배너 삭제");
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<AdminManagementResponse.PolicyResponse> getPolicies(String keyword, int page, int size) {
        List<AdminManagementResponse.PolicyResponse> items = policyRepository.findAll().stream()
                .filter(policy -> matchesKeyword(keyword, policy.getPolicyKey(), policy.getTitle(), policy.getContent()))
                .sorted(Comparator.comparing(AdminPolicy::getId).reversed())
                .map(AdminManagementResponse.PolicyResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional
    public AdminManagementResponse.PolicyResponse savePolicy(Long adminMemberId, AdminManagementRequest.PolicyUpsertRequest request) {
        AdminPolicy policy = policyRepository.findByPolicyKey(request.getPolicyKey())
                .map(existing -> {
                    existing.update(request.getTitle(), request.getContent());
                    return existing;
                })
                .orElseGet(() -> policyRepository.save(new AdminPolicy(
                        request.getPolicyKey(),
                        request.getTitle(),
                        request.getContent()
                )));
        audit(adminMemberId, "SAVE", "POLICY", policy.getId(), "정책 저장: " + policy.getPolicyKey());
        return new AdminManagementResponse.PolicyResponse(policy);
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<AdminManagementResponse.PostResponse> getPosts(
            AdminPostType type,
            String keyword,
            int page,
            int size
    ) {
        List<AdminPost> posts = type == null ? postRepository.findAll() : postRepository.findByTypeOrderByCreatedAtDesc(type);
        List<AdminManagementResponse.PostResponse> items = posts.stream()
                .filter(post -> matchesKeyword(keyword, post.getTitle(), post.getContent(), post.getAuthorLoginId()))
                .sorted(Comparator.comparing(AdminPost::getId).reversed())
                .map(AdminManagementResponse.PostResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional
    public AdminManagementResponse.PostResponse createPost(Long adminMemberId, AdminManagementRequest.PostUpsertRequest request) {
        Member admin = getMember(adminMemberId);
        AdminPost post = new AdminPost(request.getType(), request.getTitle(), request.getContent(), admin.getLoginId());
        AdminPost savedPost = postRepository.save(post);
        audit(adminMemberId, "CREATE", "BOARD", savedPost.getId(), "게시글 등록: " + savedPost.getTitle());
        return new AdminManagementResponse.PostResponse(savedPost);
    }

    @Transactional
    public AdminManagementResponse.PostResponse updatePost(Long adminMemberId, Long postId, AdminManagementRequest.PostUpsertRequest request) {
        AdminPost post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        post.update(request.getType(), request.getTitle(), request.getContent());
        audit(adminMemberId, "UPDATE", "BOARD", postId, "게시글 수정: " + post.getTitle());
        return new AdminManagementResponse.PostResponse(post);
    }

    @Transactional
    public void deletePost(Long adminMemberId, Long postId) {
        postRepository.deleteById(postId);
        audit(adminMemberId, "DELETE", "BOARD", postId, "게시글 삭제");
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<AdminManagementResponse.InquiryResponse> getInquiries(
            AdminInquiryStatus status,
            String keyword,
            int page,
            int size
    ) {
        List<AdminManagementResponse.InquiryResponse> items = inquiryRepository.findAll().stream()
                .filter(inquiry -> status == null || inquiry.getStatus() == status)
                .filter(inquiry -> matchesKeyword(keyword, inquiry.getTitle(), inquiry.getContent(), inquiry.getAuthorLoginId()))
                .sorted(Comparator.comparing(AdminInquiry::getId).reversed())
                .map(AdminManagementResponse.InquiryResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional
    public AdminManagementResponse.InquiryResponse createInquiry(Long memberId, AdminManagementRequest.InquiryCreateRequest request) {
        Member member = getMember(memberId);
        AdminInquiry inquiry = new AdminInquiry(member.getLoginId(), request.getTitle(), request.getContent());
        return new AdminManagementResponse.InquiryResponse(inquiryRepository.save(inquiry));
    }

    @Transactional
    public AdminManagementResponse.InquiryResponse answerInquiry(Long adminMemberId, Long inquiryId, AdminManagementRequest.InquiryAnswerRequest request) {
        AdminInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        inquiry.answer(request.getAnswer());
        audit(adminMemberId, "ANSWER", "INQUIRY", inquiryId, "문의 답변: " + inquiry.getTitle());
        return new AdminManagementResponse.InquiryResponse(inquiry);
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<AdminManagementResponse.ReportResponse> getReports(
            AdminReportTargetType targetType,
            AdminReportStatus status,
            String keyword,
            int page,
            int size
    ) {
        List<AdminReport> reports = targetType == null ? reportRepository.findAll() : reportRepository.findByTargetTypeOrderByCreatedAtDesc(targetType);
        List<AdminManagementResponse.ReportResponse> items = reports.stream()
                .filter(report -> status == null || report.getStatus() == status)
                .filter(report -> matchesKeyword(keyword, report.getReason(), String.valueOf(report.getTargetId())))
                .sorted(Comparator.comparing(AdminReport::getId).reversed())
                .map(AdminManagementResponse.ReportResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional
    public AdminManagementResponse.ReportResponse createReport(Long adminMemberId, AdminManagementRequest.ReportCreateRequest request) {
        AdminReport report = new AdminReport(request.getTargetType(), request.getTargetId(), request.getReason());
        AdminReport savedReport = reportRepository.save(report);
        audit(adminMemberId, "CREATE", "REPORT", savedReport.getId(), "신고 등록: " + savedReport.getTargetType());
        return new AdminManagementResponse.ReportResponse(savedReport);
    }

    @Transactional
    public AdminManagementResponse.ReportResponse resolveReport(Long adminMemberId, Long reportId) {
        AdminReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        report.resolve();
        audit(adminMemberId, "RESOLVE", "REPORT", reportId, "신고 처리 완료");
        return new AdminManagementResponse.ReportResponse(report);
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<AdminManagementResponse.SettlementResponse> getSettlements(
            AdminSettlementStatus status,
            String keyword,
            int page,
            int size
    ) {
        List<AdminManagementResponse.SettlementResponse> items = settlementRepository.findAll().stream()
                .filter(settlement -> status == null || settlement.getStatus() == status)
                .filter(settlement -> matchesKeyword(keyword, settlement.getSellerProfile().getStoreName()))
                .sorted(Comparator.comparing(AdminSettlement::getId).reversed())
                .map(AdminManagementResponse.SettlementResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional
    public AdminManagementResponse.SettlementResponse createSettlement(Long adminMemberId, AdminManagementRequest.SettlementCreateRequest request) {
        SellerProfile sellerProfile = sellerProfileRepository.findById(request.getSellerProfileId())
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_PROFILE_NOT_FOUND));
        AdminSettlement settlement = new AdminSettlement(sellerProfile, request.getAmount());
        AdminSettlement savedSettlement = settlementRepository.save(settlement);
        audit(adminMemberId, "CREATE", "SETTLEMENT", savedSettlement.getId(), "정산 등록: " + sellerProfile.getStoreName());
        return new AdminManagementResponse.SettlementResponse(savedSettlement);
    }

    @Transactional
    public AdminManagementResponse.SettlementResponse completeSettlement(Long adminMemberId, Long settlementId) {
        AdminSettlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        settlement.complete();
        audit(adminMemberId, "COMPLETE", "SETTLEMENT", settlementId, "정산 완료: " + settlement.getSellerProfile().getStoreName());
        return new AdminManagementResponse.SettlementResponse(settlement);
    }

    @Transactional(readOnly = true)
    public AdminManagementResponse.PageResponse<AdminManagementResponse.AuditLogResponse> getAuditLogs(
            String keyword,
            int page,
            int size
    ) {
        List<AdminManagementResponse.AuditLogResponse> items = auditLogRepository.findAll().stream()
                .filter(log -> matchesKeyword(keyword, log.getAdminLoginId(), log.getAction(), log.getTargetType(), log.getDescription()))
                .sorted(Comparator.comparing(AdminAuditLog::getId).reversed())
                .map(AdminManagementResponse.AuditLogResponse::new)
                .toList();
        return toPage(items, page, size);
    }

    @Transactional(readOnly = true)
    public long getPendingSettlementAmount() {
        return settlementRepository.sumAmountByStatus(AdminSettlementStatus.PENDING);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
    }

    private void audit(Long adminMemberId, String action, String targetType, Long targetId, String description) {
        Member admin = getMember(adminMemberId);
        auditLogRepository.save(new AdminAuditLog(
                admin.getId(),
                admin.getLoginId(),
                action,
                targetType,
                targetId,
                description
        ));
    }

    private boolean matchesKeyword(String keyword, String... values) {
        if (isBlank(keyword)) {
            return true;
        }

        String lowerKeyword = keyword.toLowerCase();
        for (String value : values) {
            if (value != null && value.toLowerCase().contains(lowerKeyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private <T> AdminManagementResponse.PageResponse<T> toPage(List<T> source, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        int fromIndex = Math.min(normalizedPage * normalizedSize, source.size());
        int toIndex = Math.min(fromIndex + normalizedSize, source.size());
        int totalPages = source.isEmpty() ? 0 : (int) Math.ceil((double) source.size() / normalizedSize);

        return new AdminManagementResponse.PageResponse<>(
                source.subList(fromIndex, toIndex),
                normalizedPage,
                normalizedSize,
                source.size(),
                totalPages
        );
    }
}
