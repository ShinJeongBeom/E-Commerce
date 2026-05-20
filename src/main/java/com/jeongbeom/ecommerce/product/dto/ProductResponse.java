package com.jeongbeom.ecommerce.product.dto;

import com.jeongbeom.ecommerce.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String plantType;
    private String careLevel;
    private String lightRequirement;
    private String wateringCycle;
    private String imageUrl;
    private String potIncluded;
    private String description;
    private int price;
    private int stock;
    private String status;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.plantType = product.getPlantType();
        this.careLevel = product.getCareLevel().name();
        this.lightRequirement = product.getLightRequirement().name();
        this.wateringCycle = product.getWateringCycle().name();
        this.imageUrl = product.getImageUrl();
        this.potIncluded = product.getPotIncluded();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.status = product.getStatus().name();
    }
}
