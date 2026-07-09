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
public class AdminPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminPostType type;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String authorLoginId;

    public AdminPost(AdminPostType type, String title, String content, String authorLoginId) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.authorLoginId = authorLoginId;
    }

    public void update(AdminPostType type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
    }
}
