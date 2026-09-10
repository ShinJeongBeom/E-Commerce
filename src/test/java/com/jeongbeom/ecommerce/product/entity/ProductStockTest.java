package com.jeongbeom.ecommerce.product.entity;

import com.jeongbeom.ecommerce.product.exception.InvalidStockQuantityException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductStockTest {

    @Test
    @DisplayName("재고가 모두 차감되면 품절되고 재고 복구 시 다시 판매 상태가 된다")
    void stockStatusFollowsStockQuantity() {
        Product product = createProduct(2);

        product.decreaseStock(2);

        assertThat(product.getStock()).isZero();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);

        product.increaseStock(2);

        assertThat(product.getStock()).isEqualTo(2);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("재고 복구 수량은 1개 이상이어야 한다")
    void stockIncreaseQuantityMustBePositive() {
        Product product = createProduct(2);

        assertThatThrownBy(() -> product.increaseStock(0))
                .isInstanceOf(InvalidStockQuantityException.class);
        assertThatThrownBy(() -> product.increaseStock(-1))
                .isInstanceOf(InvalidStockQuantityException.class);
        assertThat(product.getStock()).isEqualTo(2);
    }

    private Product createProduct(int stock) {
        return new Product(
                "방울복랑금",
                "다육식물",
                CareLevel.NORMAL,
                LightRequirement.MEDIUM,
                WateringCycle.WEEKLY,
                "https://example.com/product.jpg",
                "화분 포함",
                "상품 설명",
                5000,
                stock,
                ProductStatus.ON_SALE
        );
    }
}
