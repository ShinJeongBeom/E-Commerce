package com.jeongbeom.ecommerce.member.entity;

import com.jeongbeom.ecommerce.cart.entity.Cart;
import com.jeongbeom.ecommerce.common.entity.BaseTimeEntity;
import com.jeongbeom.ecommerce.common.entity.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // 기본 생성자 자동 생성, 외부 생성 차단
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //PK

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(mappedBy = "member")
    private Cart cart;

    public Member(String email, String password, String phone, Role role) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }
}
