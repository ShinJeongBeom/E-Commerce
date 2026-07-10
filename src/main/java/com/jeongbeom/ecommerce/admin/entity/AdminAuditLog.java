package com.jeongbeom.ecommerce.admin.entity;

import com.jeongbeom.ecommerce.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AdminAuditLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long adminMemberId;

    @Column(nullable = false)
    private String adminLoginId;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String targetType;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false, length = 1000)
    private String description;

    public AdminAuditLog(
            Long adminMemberId,
            String adminLoginId,
            String action,
            String targetType,
            Long targetId,
            String description
    ) {
        this.adminMemberId = adminMemberId;
        this.adminLoginId = adminLoginId;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.description = description;
    }
}
