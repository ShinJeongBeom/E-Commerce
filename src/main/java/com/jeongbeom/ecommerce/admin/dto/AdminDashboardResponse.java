package com.jeongbeom.ecommerce.admin.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AdminDashboardResponse {
    private final String loginId;
    private final String today;
    private final String domainExpiresAt;
    private final long domainDday;
    private final List<QuickMenuResponse> quickMenus;
    private final TodayStatusResponse todayStatus;
    private final PendingStatusResponse pendingStatus;
    private final List<BoardPostResponse> improvementPosts;
    private final List<BoardPostResponse> manualPosts;

    public AdminDashboardResponse(
            String loginId,
            String today,
            String domainExpiresAt,
            long domainDday,
            List<QuickMenuResponse> quickMenus,
            TodayStatusResponse todayStatus,
            PendingStatusResponse pendingStatus,
            List<BoardPostResponse> improvementPosts,
            List<BoardPostResponse> manualPosts
    ) {
        this.loginId = loginId;
        this.today = today;
        this.domainExpiresAt = domainExpiresAt;
        this.domainDday = domainDday;
        this.quickMenus = quickMenus;
        this.todayStatus = todayStatus;
        this.pendingStatus = pendingStatus;
        this.improvementPosts = improvementPosts;
        this.manualPosts = manualPosts;
    }

    @Getter
    public static class QuickMenuResponse {
        private final String label;
        private final String target;

        public QuickMenuResponse(String label, String target) {
            this.label = label;
            this.target = target;
        }
    }

    @Getter
    public static class TodayStatusResponse {
        private final long memberSignupCount;
        private final long memberWithdrawalCount;
        private final long productCreatedCount;
        private final long pageViewCount;
        private final long orderCount;

        public TodayStatusResponse(
                long memberSignupCount,
                long memberWithdrawalCount,
                long productCreatedCount,
                long pageViewCount,
                long orderCount
        ) {
            this.memberSignupCount = memberSignupCount;
            this.memberWithdrawalCount = memberWithdrawalCount;
            this.productCreatedCount = productCreatedCount;
            this.pageViewCount = pageViewCount;
            this.orderCount = orderCount;
        }
    }

    @Getter
    public static class PendingStatusResponse {
        private final long productReportCount;
        private final long exchangeRefundCount;
        private final long oneToOneInquiryCount;
        private final long productInquiryCount;
        private final long sellerApprovalCount;
        private final long orderProcessingCount;

        public PendingStatusResponse(
                long productReportCount,
                long exchangeRefundCount,
                long oneToOneInquiryCount,
                long productInquiryCount,
                long sellerApprovalCount,
                long orderProcessingCount
        ) {
            this.productReportCount = productReportCount;
            this.exchangeRefundCount = exchangeRefundCount;
            this.oneToOneInquiryCount = oneToOneInquiryCount;
            this.productInquiryCount = productInquiryCount;
            this.sellerApprovalCount = sellerApprovalCount;
            this.orderProcessingCount = orderProcessingCount;
        }
    }

    @Getter
    public static class BoardPostResponse {
        private final String title;
        private final String authorLoginId;
        private final String createdDate;

        public BoardPostResponse(String title, String authorLoginId, String createdDate) {
            this.title = title;
            this.authorLoginId = authorLoginId;
            this.createdDate = createdDate;
        }
    }
}
