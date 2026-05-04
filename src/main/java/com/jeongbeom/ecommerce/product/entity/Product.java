package com.jeongbeom.ecommerce.product.entity;

import com.jeongbeom.ecommerce.common.entity.BaseTimeEntity;
import com.jeongbeom.ecommerce.product.exception.InvalidStockQuantityException;
import com.jeongbeom.ecommerce.product.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //PK

    @Column(nullable = false)
    private String name; //상품 이름

    @Column(nullable = false)
    private String description; //상품 상세 설명

    @Column(nullable = false)
    private int price;  //상품 가격

    @Column(nullable = false)
    private int stock;  //상품 재고 수량

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;  //상품 상태

    // 생성자
    public Product(String name, String description, int price, int stock, ProductStatus status) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    // 재고 감소
    public void decreaseStock(int quantity){
        if (quantity <= 0){
            throw new InvalidStockQuantityException();
        }

        if (stock < quantity){
           throw new NotEnoughStockException();
        }

        this.stock -= quantity;
    }

    // 재고 증가
    public void increaseStock(int quantity){
        this.stock += quantity;
    }




}
