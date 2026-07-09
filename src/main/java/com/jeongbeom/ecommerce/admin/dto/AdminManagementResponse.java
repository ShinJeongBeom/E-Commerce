package com.jeongbeom.ecommerce.admin.dto;

import com.jeongbeom.ecommerce.admin.entity.AdminBanner;
import com.jeongbeom.ecommerce.admin.entity.AdminInquiry;
import com.jeongbeom.ecommerce.admin.entity.AdminPolicy;
import com.jeongbeom.ecommerce.admin.entity.AdminPost;
import com.jeongbeom.ecommerce.admin.entity.AdminReport;
import com.jeongbeom.ecommerce.admin.entity.AdminSettlement;
import com.jeongbeom.ecommerce.member.entity.Member;
import lombok.Getter;

public class AdminManagementResponse {

    @Getter
    public static class MemberResponse {
        private final Long id;
        private final String loginId;
        private final String email;
        private final String phone;
        private final String role;
        private final String status;
        private final String createdAt;

        public MemberResponse(Member member) {
            this.id = member.getId();
            this.loginId = member.getLoginId();
            this.email = member.getEmail();
            this.phone = member.getPhone();
            this.role = member.getRole().name();
            this.status = member.getStatus().name();
            this.createdAt = member.getCreatedAt() == null ? null : member.getCreatedAt().toString();
        }
    }

    @Getter
    public static class BannerResponse {
        private final Long id;
        private final String title;
        private final String imageUrl;
        private final String linkUrl;
        private final boolean visible;
        private final int sortOrder;

        public BannerResponse(AdminBanner banner) {
            this.id = banner.getId();
            this.title = banner.getTitle();
            this.imageUrl = banner.getImageUrl();
            this.linkUrl = banner.getLinkUrl();
            this.visible = banner.isVisible();
            this.sortOrder = banner.getSortOrder();
        }
    }

    @Getter
    public static class PolicyResponse {
        private final Long id;
        private final String policyKey;
        private final String title;
        private final String content;

        public PolicyResponse(AdminPolicy policy) {
            this.id = policy.getId();
            this.policyKey = policy.getPolicyKey();
            this.title = policy.getTitle();
            this.content = policy.getContent();
        }
    }

    @Getter
    public static class PostResponse {
        private final Long id;
        private final String type;
        private final String title;
        private final String content;
        private final String authorLoginId;
        private final String createdAt;

        public PostResponse(AdminPost post) {
            this.id = post.getId();
            this.type = post.getType().name();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.authorLoginId = post.getAuthorLoginId();
            this.createdAt = post.getCreatedAt() == null ? null : post.getCreatedAt().toString();
        }
    }

    @Getter
    public static class InquiryResponse {
        private final Long id;
        private final String authorLoginId;
        private final String title;
        private final String content;
        private final String answer;
        private final String status;

        public InquiryResponse(AdminInquiry inquiry) {
            this.id = inquiry.getId();
            this.authorLoginId = inquiry.getAuthorLoginId();
            this.title = inquiry.getTitle();
            this.content = inquiry.getContent();
            this.answer = inquiry.getAnswer();
            this.status = inquiry.getStatus().name();
        }
    }

    @Getter
    public static class ReportResponse {
        private final Long id;
        private final String targetType;
        private final Long targetId;
        private final String reason;
        private final String status;

        public ReportResponse(AdminReport report) {
            this.id = report.getId();
            this.targetType = report.getTargetType().name();
            this.targetId = report.getTargetId();
            this.reason = report.getReason();
            this.status = report.getStatus().name();
        }
    }

    @Getter
    public static class SettlementResponse {
        private final Long id;
        private final Long sellerProfileId;
        private final String storeName;
        private final long amount;
        private final String status;

        public SettlementResponse(AdminSettlement settlement) {
            this.id = settlement.getId();
            this.sellerProfileId = settlement.getSellerProfile().getId();
            this.storeName = settlement.getSellerProfile().getStoreName();
            this.amount = settlement.getAmount();
            this.status = settlement.getStatus().name();
        }
    }
}
