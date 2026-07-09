package com.jeongbeom.ecommerce.admin.dto;

import com.jeongbeom.ecommerce.admin.entity.AdminPostType;
import com.jeongbeom.ecommerce.admin.entity.AdminReportTargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminManagementRequest {

    @Getter
    @NoArgsConstructor
    public static class BannerUpsertRequest {
        private String title;
        private String imageUrl;
        private String linkUrl;
        private boolean visible;
        private int sortOrder;
    }

    @Getter
    @NoArgsConstructor
    public static class PolicyUpsertRequest {
        private String policyKey;
        private String title;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class PostUpsertRequest {
        private AdminPostType type;
        private String title;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class InquiryCreateRequest {
        private String title;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class InquiryAnswerRequest {
        private String answer;
    }

    @Getter
    @NoArgsConstructor
    public static class ReportCreateRequest {
        private AdminReportTargetType targetType;
        private Long targetId;
        private String reason;
    }

    @Getter
    @NoArgsConstructor
    public static class SettlementCreateRequest {
        private Long sellerProfileId;
        private long amount;
    }
}
