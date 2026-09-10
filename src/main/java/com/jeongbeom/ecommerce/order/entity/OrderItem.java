package com.jeongbeom.ecommerce.order.entity;

import com.jeongbeom.ecommerce.common.entity.BaseTimeEntity;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.exception.InvalidStockQuantityException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private int orderPrice;

    @Column(nullable = false)
    private String productNameSnapshot;

    @Column(nullable = false)
    private int orderQuantity;

    public OrderItem(Order order, Product product, int orderQuantity) {
        if (orderQuantity <= 0) {
            throw new InvalidStockQuantityException();
        }
        this.order = order;
        this.product = product;
        this.orderQuantity = orderQuantity;
        this.orderPrice = product.getPrice();
        this.productNameSnapshot = product.getName();
    }

    public int calculateTotalPrice() {
        return Math.multiplyExact(orderPrice, orderQuantity);
    }
}
