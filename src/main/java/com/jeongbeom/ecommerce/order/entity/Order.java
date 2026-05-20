package com.jeongbeom.ecommerce.order.entity;

import com.jeongbeom.ecommerce.common.entity.BaseTimeEntity;
import com.jeongbeom.ecommerce.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //PK

    //Member 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    public Order(Member member, String orderNumber, int totalPrice, OrderStatus status, String name, String phone, String address) {
        this.member = member;
        this.orderNumber = orderNumber;
        this.totalPrice = totalPrice;
        this.status = status;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    //총액 변경 매서드
    public void changeTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
