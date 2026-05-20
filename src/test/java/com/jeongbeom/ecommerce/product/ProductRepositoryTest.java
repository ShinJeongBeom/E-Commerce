package com.jeongbeom.ecommerce.product;

import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void 상품_저장_및_조회_테스트() {
        // given
        Product product = new Product(
                "아이폰",
                "애플 스마트폰",
                1000000,
                10,
                ProductStatus.ON_SALE
        );

        // when
        Product saved = productRepository.save(product);

        Product found = productRepository.findById(saved.getId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        // then
        assert saved.getId().equals(found.getId());
        assert saved.getName().equals(found.getName());
        assert saved.getPrice() == found.getPrice();
    }

    @Test
    void 재고_감소_테스트() {
        Product product = new Product(
                "아이폰",
                "애플 스마트폰",
                1000000,
                10,
                ProductStatus.ON_SALE
        );

        product.decreaseStock(3);

        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    void 재고_부족_예외() {
        Product product = new Product(
                "아이폰",
                "애플 스마트폰",
                1000000,
                5,
                ProductStatus.ON_SALE
        );

        assertThatThrownBy(() -> product.decreaseStock(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 부족");
    }
}
