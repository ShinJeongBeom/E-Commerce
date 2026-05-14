package com.jeongbeom.ecommerce.product;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.dto.OrderCreateRequestDto;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.order.service.OrderService;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.product.exception.InvalidStockQuantityException;
import com.jeongbeom.ecommerce.product.exception.NotEnoughStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderService orderService;

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

    @Test
    @DisplayName("재고가 부족하면 주문이 저장되지 않는다")
    void 재고_부족_주문_실패_테스트() {
        Member member = memberRepository.save(
                new Member("stock@test.com", "1234", "010-1111-2222", Role.USER)
        );

        Product product = productRepository.save(
                new Product("후드티", "검정 후드티", 70000, 1, ProductStatus.ON_SALE)
        );

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                product.getId(),
                2,
                "신정범",
                "010-1111-2222",
                "서울시 강남구"
        );

        assertThatThrownBy(() -> orderService.createOrder(member.getId(), requestDto))
                .isInstanceOf(NotEnoughStockException.class);

        assertThat(orderRepository.findByMember(member)).isEmpty();
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(1);
    }

    @Test
    @DisplayName("주문 수량이 0 이하이면 주문이 저장되지 않는다")
    void 잘못된_주문_수량_실패_테스트() {
        Member member = memberRepository.save(
                new Member("quantity@test.com", "1234", "010-1111-2222", Role.USER)
        );

        Product product = productRepository.save(
                new Product("반팔 티셔츠", "흰색 반팔 티셔츠", 30000, 10, ProductStatus.ON_SALE)
        );

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                product.getId(),
                0,
                "신정범",
                "010-1111-2222",
                "서울시 강남구"
        );

        assertThatThrownBy(() -> orderService.createOrder(member.getId(), requestDto))
                .isInstanceOf(InvalidStockQuantityException.class);

        assertThat(orderRepository.findByMember(member)).isEmpty();
        assertThat(productRepository.findById(product.getId()).orElseThrow().getStock()).isEqualTo(10);
    }


}
