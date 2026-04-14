package com.jeongbeom.ecommerce.cart.entity;

import com.jeongbeom.ecommerce.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // 연관관계 설정
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    // 수량 증가
    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
    }

    // 수량 변경
    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }


}