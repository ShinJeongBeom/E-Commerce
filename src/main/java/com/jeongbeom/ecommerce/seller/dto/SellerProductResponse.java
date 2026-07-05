package com.jeongbeom.ecommerce.seller.dto;

import com.jeongbeom.ecommerce.product.entity.Product;
import lombok.Getter;

@Getter
public class SellerProductResponse {
    private final Long id;
    private final String name;
    private final int price;
    private final int stock;
    private final String status;
    private final String imageUrl;

    public SellerProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.status = product.getStatus().name();
        this.imageUrl = product.getImageUrl();
    }
}
