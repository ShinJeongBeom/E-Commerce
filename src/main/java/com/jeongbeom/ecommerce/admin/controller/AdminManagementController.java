package com.jeongbeom.ecommerce.admin.controller;

import com.jeongbeom.ecommerce.admin.dto.AdminManagementRequest;
import com.jeongbeom.ecommerce.admin.dto.AdminManagementResponse;
import com.jeongbeom.ecommerce.admin.entity.AdminPostType;
import com.jeongbeom.ecommerce.admin.entity.AdminReportTargetType;
import com.jeongbeom.ecommerce.admin.service.AdminManagementService;
import com.jeongbeom.ecommerce.order.dto.OrderResponseDto;
import com.jeongbeom.ecommerce.product.dto.ProductResponse;
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
    public List<OrderResponseDto> getOrders() {
        return adminManagementService.getOrders();
    }

    @GetMapping("/members")
    public List<AdminManagementResponse.MemberResponse> getMembers() {
        return adminManagementService.getMembers();
    }

    @PatchMapping("/members/{memberId}/suspend")
    public AdminManagementResponse.MemberResponse suspendMember(@PathVariable Long memberId) {
        return adminManagementService.suspendMember(memberId);
    }

    @DeleteMapping("/members/{memberId}")
    public AdminManagementResponse.MemberResponse deleteMember(@PathVariable Long memberId) {
        return adminManagementService.deleteMember(memberId);
    }

    @GetMapping("/products")
    public List<ProductResponse> getProducts() {
        return adminManagementService.getProducts();
    }

    @PatchMapping("/products/{productId}/hide")
    public ProductResponse hideProduct(@PathVariable Long productId) {
        return adminManagementService.hideProduct(productId);
    }

    @PatchMapping("/products/{productId}/restore")
    public ProductResponse restoreProduct(@PathVariable Long productId) {
        return adminManagementService.restoreProduct(productId);
    }

    @DeleteMapping("/products/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        adminManagementService.deleteProduct(productId);
    }

    @GetMapping("/banners")
    public List<AdminManagementResponse.BannerResponse> getBanners() {
        return adminManagementService.getBanners();
    }

    @PostMapping("/banners")
    public AdminManagementResponse.BannerResponse createBanner(
            @RequestBody AdminManagementRequest.BannerUpsertRequest request
    ) {
        return adminManagementService.createBanner(request);
    }

    @PutMapping("/banners/{bannerId}")
    public AdminManagementResponse.BannerResponse updateBanner(
            @PathVariable Long bannerId,
            @RequestBody AdminManagementRequest.BannerUpsertRequest request
    ) {
        return adminManagementService.updateBanner(bannerId, request);
    }

    @DeleteMapping("/banners/{bannerId}")
    public void deleteBanner(@PathVariable Long bannerId) {
        adminManagementService.deleteBanner(bannerId);
    }

    @GetMapping("/policies")
    public List<AdminManagementResponse.PolicyResponse> getPolicies() {
        return adminManagementService.getPolicies();
    }

    @PostMapping("/policies")
    public AdminManagementResponse.PolicyResponse savePolicy(
            @RequestBody AdminManagementRequest.PolicyUpsertRequest request
    ) {
        return adminManagementService.savePolicy(request);
    }

    @GetMapping("/boards")
    public List<AdminManagementResponse.PostResponse> getPosts(
            @RequestParam(required = false) AdminPostType type
    ) {
        return adminManagementService.getPosts(type);
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
            @PathVariable Long postId,
            @RequestBody AdminManagementRequest.PostUpsertRequest request
    ) {
        return adminManagementService.updatePost(postId, request);
    }

    @DeleteMapping("/boards/{postId}")
    public void deletePost(@PathVariable Long postId) {
        adminManagementService.deletePost(postId);
    }

    @GetMapping("/inquiries")
    public List<AdminManagementResponse.InquiryResponse> getInquiries() {
        return adminManagementService.getInquiries();
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
            @PathVariable Long inquiryId,
            @RequestBody AdminManagementRequest.InquiryAnswerRequest request
    ) {
        return adminManagementService.answerInquiry(inquiryId, request);
    }

    @GetMapping("/reports")
    public List<AdminManagementResponse.ReportResponse> getReports(
            @RequestParam(required = false) AdminReportTargetType targetType
    ) {
        return adminManagementService.getReports(targetType);
    }

    @PostMapping("/reports")
    public AdminManagementResponse.ReportResponse createReport(
            @RequestBody AdminManagementRequest.ReportCreateRequest request
    ) {
        return adminManagementService.createReport(request);
    }

    @PatchMapping("/reports/{reportId}/resolve")
    public AdminManagementResponse.ReportResponse resolveReport(@PathVariable Long reportId) {
        return adminManagementService.resolveReport(reportId);
    }

    @GetMapping("/settlements")
    public List<AdminManagementResponse.SettlementResponse> getSettlements() {
        return adminManagementService.getSettlements();
    }

    @PostMapping("/settlements")
    public AdminManagementResponse.SettlementResponse createSettlement(
            @RequestBody AdminManagementRequest.SettlementCreateRequest request
    ) {
        return adminManagementService.createSettlement(request);
    }

    @PatchMapping("/settlements/{settlementId}/complete")
    public AdminManagementResponse.SettlementResponse completeSettlement(@PathVariable Long settlementId) {
        return adminManagementService.completeSettlement(settlementId);
    }

    private Long getMemberId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
