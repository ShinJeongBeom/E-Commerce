package com.jeongbeom.ecommerce.admin.entity;

import com.jeongbeom.ecommerce.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AdminInquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String authorLoginId;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Lob
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminInquiryStatus status;

    public AdminInquiry(String authorLoginId, String title, String content) {
        this.authorLoginId = authorLoginId;
        this.title = title;
        this.content = content;
        this.status = AdminInquiryStatus.WAITING;
    }

    public void answer(String answer) {
        this.answer = answer;
        this.status = AdminInquiryStatus.ANSWERED;
    }
}
