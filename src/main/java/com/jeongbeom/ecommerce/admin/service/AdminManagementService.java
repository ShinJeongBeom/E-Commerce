package com.jeongbeom.ecommerce.admin.service;

import com.jeongbeom.ecommerce.admin.dto.AdminManagementRequest;
import com.jeongbeom.ecommerce.admin.dto.AdminManagementResponse;
import com.jeongbeom.ecommerce.admin.entity.AdminBanner;
import com.jeongbeom.ecommerce.admin.entity.AdminInquiry;
import com.jeongbeom.ecommerce.admin.entity.AdminPolicy;
import com.jeongbeom.ecommerce.admin.entity.AdminPost;
import com.jeongbeom.ecommerce.admin.entity.AdminPostType;
import com.jeongbeom.ecommerce.admin.entity.AdminReport;
import com.jeongbeom.ecommerce.admin.entity.AdminReportTargetType;
import com.jeongbeom.ecommerce.admin.entity.AdminSettlement;
import com.jeongbeom.ecommerce.admin.entity.AdminSettlementStatus;
import com.jeongbeom.ecommerce.admin.repository.AdminBannerRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminInquiryRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminPolicyRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminPostRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminReportRepository;
import com.jeongbeom.ecommerce.admin.repository.AdminSettlementRepository;
import com.jeongbeom.ecommerce.common.exception.CustomException;
import com.jeongbeom.ecommerce.common.exception.ErrorCode;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.dto.OrderResponseDto;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.product.dto.ProductResponse;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.product.exception.ProductNotFoundException;
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
    private final AdminBannerRepository bannerRepository;
    private final AdminPolicyRepository policyRepository;
    private final AdminPostRepository postRepository;
    private final AdminInquiryRepository inquiryRepository;
    private final AdminReportRepository reportRepository;
    private final AdminSettlementRepository settlementRepository;

    @Transactional(readOnly = true)
    public List<AdminManagementResponse.MemberResponse> getMembers() {
        return memberRepository.findAll().stream()
                .sorted(Comparator.comparing(Member::getId).reversed())
                .map(AdminManagementResponse.MemberResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrders() {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getId).reversed())
                .map(OrderResponseDto::new)
                .toList();
    }

    @Transactional
    public AdminManagementResponse.MemberResponse suspendMember(Long memberId) {
        Member member = getMember(memberId);
        member.suspend();
        return new AdminManagementResponse.MemberResponse(member);
    }

    @Transactional
    public AdminManagementResponse.MemberResponse deleteMember(Long memberId) {
        Member member = getMember(memberId);
        member.deleteAccount();
        return new AdminManagementResponse.MemberResponse(member);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getId).reversed())
                .map(ProductResponse::new)
                .toList();
    }

    @Transactional
    public ProductResponse hideProduct(Long productId) {
        Product product = getProduct(productId);
        product.hide();
        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse restoreProduct(Long productId) {
        Product product = getProduct(productId);
        product.restore();
        return new ProductResponse(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.delete(getProduct(productId));
    }

    @Transactional(readOnly = true)
    public List<AdminManagementResponse.BannerResponse> getBanners() {
        return bannerRepository.findAllByOrderBySortOrderAscIdDesc().stream()
                .map(AdminManagementResponse.BannerResponse::new)
                .toList();
    }

    @Transactional
    public AdminManagementResponse.BannerResponse createBanner(AdminManagementRequest.BannerUpsertRequest request) {
        AdminBanner banner = new AdminBanner(
                request.getTitle(),
                request.getImageUrl(),
                request.getLinkUrl(),
                request.isVisible(),
                request.getSortOrder()
        );
        return new AdminManagementResponse.BannerResponse(bannerRepository.save(banner));
    }

    @Transactional
    public AdminManagementResponse.BannerResponse updateBanner(Long bannerId, AdminManagementRequest.BannerUpsertRequest request) {
        AdminBanner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        banner.update(request.getTitle(), request.getImageUrl(), request.getLinkUrl(), request.isVisible(), request.getSortOrder());
        return new AdminManagementResponse.BannerResponse(banner);
    }

    @Transactional
    public void deleteBanner(Long bannerId) {
        bannerRepository.deleteById(bannerId);
    }

    @Transactional(readOnly = true)
    public List<AdminManagementResponse.PolicyResponse> getPolicies() {
        return policyRepository.findAll().stream()
                .sorted(Comparator.comparing(AdminPolicy::getId).reversed())
                .map(AdminManagementResponse.PolicyResponse::new)
                .toList();
    }

    @Transactional
    public AdminManagementResponse.PolicyResponse savePolicy(AdminManagementRequest.PolicyUpsertRequest request) {
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
        return new AdminManagementResponse.PolicyResponse(policy);
    }

    @Transactional(readOnly = true)
    public List<AdminManagementResponse.PostResponse> getPosts(AdminPostType type) {
        List<AdminPost> posts = type == null ? postRepository.findAll() : postRepository.findByTypeOrderByCreatedAtDesc(type);
        return posts.stream()
                .sorted(Comparator.comparing(AdminPost::getId).reversed())
                .map(AdminManagementResponse.PostResponse::new)
                .toList();
    }

    @Transactional
    public AdminManagementResponse.PostResponse createPost(Long adminMemberId, AdminManagementRequest.PostUpsertRequest request) {
        Member admin = getMember(adminMemberId);
        AdminPost post = new AdminPost(request.getType(), request.getTitle(), request.getContent(), admin.getLoginId());
        return new AdminManagementResponse.PostResponse(postRepository.save(post));
    }

    @Transactional
    public AdminManagementResponse.PostResponse updatePost(Long postId, AdminManagementRequest.PostUpsertRequest request) {
        AdminPost post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        post.update(request.getType(), request.getTitle(), request.getContent());
        return new AdminManagementResponse.PostResponse(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Transactional(readOnly = true)
    public List<AdminManagementResponse.InquiryResponse> getInquiries() {
        return inquiryRepository.findAll().stream()
                .sorted(Comparator.comparing(AdminInquiry::getId).reversed())
                .map(AdminManagementResponse.InquiryResponse::new)
                .toList();
    }

    @Transactional
    public AdminManagementResponse.InquiryResponse createInquiry(Long memberId, AdminManagementRequest.InquiryCreateRequest request) {
        Member member = getMember(memberId);
        AdminInquiry inquiry = new AdminInquiry(member.getLoginId(), request.getTitle(), request.getContent());
        return new AdminManagementResponse.InquiryResponse(inquiryRepository.save(inquiry));
    }

    @Transactional
    public AdminManagementResponse.InquiryResponse answerInquiry(Long inquiryId, AdminManagementRequest.InquiryAnswerRequest request) {
        AdminInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        inquiry.answer(request.getAnswer());
        return new AdminManagementResponse.InquiryResponse(inquiry);
    }

    @Transactional(readOnly = true)
    public List<AdminManagementResponse.ReportResponse> getReports(AdminReportTargetType targetType) {
        List<AdminReport> reports = targetType == null ? reportRepository.findAll() : reportRepository.findByTargetTypeOrderByCreatedAtDesc(targetType);
        return reports.stream()
                .sorted(Comparator.comparing(AdminReport::getId).reversed())
                .map(AdminManagementResponse.ReportResponse::new)
                .toList();
    }

    @Transactional
    public AdminManagementResponse.ReportResponse createReport(AdminManagementRequest.ReportCreateRequest request) {
        AdminReport report = new AdminReport(request.getTargetType(), request.getTargetId(), request.getReason());
        return new AdminManagementResponse.ReportResponse(reportRepository.save(report));
    }

    @Transactional
    public AdminManagementResponse.ReportResponse resolveReport(Long reportId) {
        AdminReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        report.resolve();
        return new AdminManagementResponse.ReportResponse(report);
    }

    @Transactional(readOnly = true)
    public List<AdminManagementResponse.SettlementResponse> getSettlements() {
        return settlementRepository.findAll().stream()
                .sorted(Comparator.comparing(AdminSettlement::getId).reversed())
                .map(AdminManagementResponse.SettlementResponse::new)
                .toList();
    }

    @Transactional
    public AdminManagementResponse.SettlementResponse createSettlement(AdminManagementRequest.SettlementCreateRequest request) {
        SellerProfile sellerProfile = sellerProfileRepository.findById(request.getSellerProfileId())
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_PROFILE_NOT_FOUND));
        AdminSettlement settlement = new AdminSettlement(sellerProfile, request.getAmount());
        return new AdminManagementResponse.SettlementResponse(settlementRepository.save(settlement));
    }

    @Transactional
    public AdminManagementResponse.SettlementResponse completeSettlement(Long settlementId) {
        AdminSettlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_IN_PUT_VALUE));
        settlement.complete();
        return new AdminManagementResponse.SettlementResponse(settlement);
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
}
