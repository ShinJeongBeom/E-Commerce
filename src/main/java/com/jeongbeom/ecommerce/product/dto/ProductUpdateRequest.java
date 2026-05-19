package com.jeongbeom.ecommerce.product.dto;

import com.jeongbeom.ecommerce.product.entity.CareLevel;
import com.jeongbeom.ecommerce.product.entity.LightRequirement;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.WateringCycle;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    private String name;
    private String plantType;
    private CareLevel careLevel;
    private LightRequirement lightRequirement;
    private WateringCycle wateringCycle;
    private String imageUrl;
    private String potIncluded;
    private String description;
    private int price;
    private int stock;
    private ProductStatus status;
}
