package com.jeongbeom.ecommerce.admin.controller;

import com.jeongbeom.ecommerce.admin.dto.AdminManagementRequest;
import com.jeongbeom.ecommerce.admin.dto.AdminManagementResponse;
import com.jeongbeom.ecommerce.admin.entity.AdminInquiryStatus;
import com.jeongbeom.ecommerce.admin.entity.AdminPostType;
import com.jeongbeom.ecommerce.admin.entity.AdminReportStatus;
import com.jeongbeom.ecommerce.admin.entity.AdminReportTargetType;
import com.jeongbeom.ecommerce.admin.entity.AdminSettlementStatus;
import com.jeongbeom.ecommerce.admin.service.AdminManagementService;
import com.jeongbeom.ecommerce.member.entity.MemberStatus;
import com.jeongbeom.ecommerce.order.dto.OrderResponseDto;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.product.dto.ProductResponse;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    @GetMapping("/orders")
    public AdminManagementResponse.PageResponse<OrderResponseDto> getOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getOrders(keyword, status, page, size);
    }

    @GetMapping("/members")
    public AdminManagementResponse.PageResponse<AdminManagementResponse.MemberResponse> getMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getMembers(keyword, role, status, page, size);
    }

    @PatchMapping("/members/{memberId}/suspend")
    public AdminManagementResponse.MemberResponse suspendMember(Authentication authentication, @PathVariable Long memberId) {
        return adminManagementService.suspendMember(getMemberId(authentication), memberId);
    }

    @DeleteMapping("/members/{memberId}")
    public AdminManagementResponse.MemberResponse deleteMember(Authentication authentication, @PathVariable Long memberId) {
        return adminManagementService.deleteMember(getMemberId(authentication), memberId);
    }

    @GetMapping("/products")
    public AdminManagementResponse.PageResponse<ProductResponse> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getProducts(keyword, status, page, size);
    }

    @PatchMapping("/products/{productId}/hide")
    public ProductResponse hideProduct(Authentication authentication, @PathVariable Long productId) {
        return adminManagementService.hideProduct(getMemberId(authentication), productId);
    }

    @PatchMapping("/products/{productId}/restore")
    public ProductResponse restoreProduct(Authentication authentication, @PathVariable Long productId) {
        return adminManagementService.restoreProduct(getMemberId(authentication), productId);
    }

    @DeleteMapping("/products/{productId}")
    public ProductResponse deleteProduct(Authentication authentication, @PathVariable Long productId) {
        return adminManagementService.deleteProduct(getMemberId(authentication), productId);
    }

    @GetMapping("/banners")
    public AdminManagementResponse.PageResponse<AdminManagementResponse.BannerResponse> getBanners(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean visible,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getBanners(keyword, visible, page, size);
    }

    @PostMapping("/banners")
    public AdminManagementResponse.BannerResponse createBanner(
            Authentication authentication,
            @RequestBody AdminManagementRequest.BannerUpsertRequest request
    ) {
        return adminManagementService.createBanner(getMemberId(authentication), request);
    }

    @PutMapping("/banners/{bannerId}")
    public AdminManagementResponse.BannerResponse updateBanner(
            Authentication authentication,
            @PathVariable Long bannerId,
            @RequestBody AdminManagementRequest.BannerUpsertRequest request
    ) {
        return adminManagementService.updateBanner(getMemberId(authentication), bannerId, request);
    }

    @DeleteMapping("/banners/{bannerId}")
    public void deleteBanner(Authentication authentication, @PathVariable Long bannerId) {
        adminManagementService.deleteBanner(getMemberId(authentication), bannerId);
    }

    @GetMapping("/policies")
    public AdminManagementResponse.PageResponse<AdminManagementResponse.PolicyResponse> getPolicies(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getPolicies(keyword, page, size);
    }

    @PostMapping("/policies")
    public AdminManagementResponse.PolicyResponse savePolicy(
            Authentication authentication,
            @RequestBody AdminManagementRequest.PolicyUpsertRequest request
    ) {
        return adminManagementService.savePolicy(getMemberId(authentication), request);
    }

    @GetMapping("/boards")
    public AdminManagementResponse.PageResponse<AdminManagementResponse.PostResponse> getPosts(
            @RequestParam(required = false) AdminPostType type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getPosts(type, keyword, page, size);
    }

    @PostMapping("/boards")
    public AdminManagementResponse.PostResponse createPost(
            Authentication authentication,
            @RequestBody AdminManagementRequest.PostUpsertRequest request
    ) {
        return adminManagementService.createPost(getMemberId(authentication), request);
    }

    @PutMapping("/boards/{postId}")
    public AdminManagementResponse.PostResponse updatePost(
            Authentication authentication,
            @PathVariable Long postId,
            @RequestBody AdminManagementRequest.PostUpsertRequest request
    ) {
        return adminManagementService.updatePost(getMemberId(authentication), postId, request);
    }

    @DeleteMapping("/boards/{postId}")
    public void deletePost(Authentication authentication, @PathVariable Long postId) {
        adminManagementService.deletePost(getMemberId(authentication), postId);
    }

    @GetMapping("/inquiries")
    public AdminManagementResponse.PageResponse<AdminManagementResponse.InquiryResponse> getInquiries(
            @RequestParam(required = false) AdminInquiryStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getInquiries(status, keyword, page, size);
    }

    @PostMapping("/inquiries")
    public AdminManagementResponse.InquiryResponse createInquiry(
            Authentication authentication,
            @RequestBody AdminManagementRequest.InquiryCreateRequest request
    ) {
        return adminManagementService.createInquiry(getMemberId(authentication), request);
    }

    @PatchMapping("/inquiries/{inquiryId}/answer")
    public AdminManagementResponse.InquiryResponse answerInquiry(
            Authentication authentication,
            @PathVariable Long inquiryId,
            @RequestBody AdminManagementRequest.InquiryAnswerRequest request
    ) {
        return adminManagementService.answerInquiry(getMemberId(authentication), inquiryId, request);
    }

    @GetMapping("/reports")
    public AdminManagementResponse.PageResponse<AdminManagementResponse.ReportResponse> getReports(
            @RequestParam(required = false) AdminReportTargetType targetType,
            @RequestParam(required = false) AdminReportStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getReports(targetType, status, keyword, page, size);
    }

    @PostMapping("/reports")
    public AdminManagementResponse.ReportResponse createReport(
            Authentication authentication,
            @RequestBody AdminManagementRequest.ReportCreateRequest request
    ) {
        return adminManagementService.createReport(getMemberId(authentication), request);
    }

    @PatchMapping("/reports/{reportId}/resolve")
    public AdminManagementResponse.ReportResponse resolveReport(Authentication authentication, @PathVariable Long reportId) {
        return adminManagementService.resolveReport(getMemberId(authentication), reportId);
    }

    @GetMapping("/settlements")
    public AdminManagementResponse.PageResponse<AdminManagementResponse.SettlementResponse> getSettlements(
            @RequestParam(required = false) AdminSettlementStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getSettlements(status, keyword, page, size);
    }

    @PostMapping("/settlements")
    public AdminManagementResponse.SettlementResponse createSettlement(
            Authentication authentication,
            @RequestBody AdminManagementRequest.SettlementCreateRequest request
    ) {
        return adminManagementService.createSettlement(getMemberId(authentication), request);
    }

    @PatchMapping("/settlements/{settlementId}/complete")
    public AdminManagementResponse.SettlementResponse completeSettlement(Authentication authentication, @PathVariable Long settlementId) {
        return adminManagementService.completeSettlement(getMemberId(authentication), settlementId);
    }

    @GetMapping("/audit-logs")
    public AdminManagementResponse.PageResponse<AdminManagementResponse.AuditLogResponse> getAuditLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminManagementService.getAuditLogs(keyword, page, size);
    }

    private Long getMemberId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
