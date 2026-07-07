package com.jeongbeom.ecommerce.seller.entity;

import com.jeongbeom.ecommerce.common.entity.BaseTimeEntity;
import com.jeongbeom.ecommerce.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class SellerProfile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerApprovalStatus approvalStatus;

    public SellerProfile(Member member, String storeName, SellerApprovalStatus approvalStatus) {
        this.member = member;
        this.storeName = storeName;
        this.approvalStatus = approvalStatus;
    }

    public void approve() {
        this.approvalStatus = SellerApprovalStatus.APPROVED;
    }

    public void suspend() {
        this.approvalStatus = SellerApprovalStatus.SUSPENDED;
    }
}
