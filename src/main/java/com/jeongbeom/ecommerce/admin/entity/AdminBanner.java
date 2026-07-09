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
public class AdminBanner extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false, length = 500)
    private String linkUrl;

    @Column(nullable = false)
    private boolean visible;

    @Column(nullable = false)
    private int sortOrder;

    public AdminBanner(String title, String imageUrl, String linkUrl, boolean visible, int sortOrder) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.visible = visible;
        this.sortOrder = sortOrder;
    }

    public void update(String title, String imageUrl, String linkUrl, boolean visible, int sortOrder) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.visible = visible;
        this.sortOrder = sortOrder;
    }
}
