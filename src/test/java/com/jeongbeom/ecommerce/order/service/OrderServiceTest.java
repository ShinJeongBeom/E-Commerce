package com.jeongbeom.ecommerce.order.service;

import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.dto.OrderCreateRequestDto;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderItem;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.order.entity.repository.OrderItemRepository;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.order.exception.OrderAccessDeniedException;
import com.jeongbeom.ecommerce.order.exception.OrderAlreadyCancelledException;
import com.jeongbeom.ecommerce.order.service.OrderService;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.CareLevel;
import com.jeongbeom.ecommerce.product.entity.LightRequirement;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.WateringCycle;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("주문 생성 시 주문과 주문상품이 저장되고 재고가 감소한다")
    void 주문_생성_테스트() {
        // given
        String email = "orderservice" + System.currentTimeMillis() + "@test.com";

        Member member = memberRepository.save(
                new Member(
                        email,
                        "1234",
                        "010-1111-2222",
                        Role.USER
                )
        );

        Product product = productRepository.save(
                new Product(
                        "방울복랑금",
                        "다육식물",
                        CareLevel.NORMAL,
                        LightRequirement.MEDIUM,
                        WateringCycle.WEEKLY,
                        "https:111,111",
                        "화분 포함",
                        "부분 부분 금색 빛이 도는 식물",
                        5000,
                        10,
                        ProductStatus.ON_SALE
                )
        );

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                product.getId(),
                2,
                "신정범",
                "010-1111-2222",
                "서울시 강남구"
        );

        // when
        orderService.createOrder(member.getId(), requestDto);

        // then
        List<Order> orders = orderRepository.findByMember(member);
        assertThat(orders).hasSize(1);

        Order savedOrder = orders.get(0);

        List<OrderItem> orderItems = orderItemRepository.findByOrder(savedOrder);
        assertThat(orderItems).hasSize(1);

        OrderItem savedOrderItem = orderItems.get(0);
        Product foundProduct = productRepository.findById(product.getId()).orElseThrow();

        assertThat(savedOrder.getMember().getId()).isEqualTo(member.getId());
        assertThat(savedOrder.getTotalPrice()).isEqualTo(10000);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(savedOrder.getName()).isEqualTo("신정범");
        assertThat(savedOrder.getPhone()).isEqualTo("010-1111-2222");
        assertThat(savedOrder.getAddress()).isEqualTo("서울시 강남구");

        assertThat(savedOrderItem.getOrder().getId()).isEqualTo(savedOrder.getId());
        assertThat(savedOrderItem.getProduct().getId()).isEqualTo(product.getId());
        assertThat(savedOrderItem.getOrderQuantity()).isEqualTo(2);
        assertThat(savedOrderItem.getOrderPrice()).isEqualTo(5000);

        assertThat(foundProduct.getStock()).isEqualTo(8);
    }

    @Test
    @DisplayName("상품 정보가 변경되어도 주문 당시 상품명과 가격 스냅샷은 유지된다")
    void 주문_상품_스냅샷_유지_테스트() {
        Member member = memberRepository.save(
                new Member(
                        "snapshot" + System.currentTimeMillis() + "@test.com",
                        "1234",
                        "010-1111-2222",
                        Role.USER
                )
        );

        Product product = productRepository.save(
                new Product(
                        "주문 당시 상품명",
                        "다육식물",
                        CareLevel.NORMAL,
                        LightRequirement.MEDIUM,
                        WateringCycle.WEEKLY,
                        "https://example.com/original.jpg",
                        "화분 포함",
                        "상품 설명",
                        5000,
                        10,
                        ProductStatus.ON_SALE
                )
        );

        orderService.createOrder(
                member.getId(),
                new OrderCreateRequestDto(
                        product.getId(),
                        2,
                        "신정범",
                        "010-1111-2222",
                        "서울시 강남구"
                )
        );

        Order savedOrder = orderRepository.findByMember(member).get(0);
        product.update(
                "변경된 상품명",
                product.getPlantType(),
                product.getCareLevel(),
                product.getLightRequirement(),
                product.getWateringCycle(),
                product.getImageUrl(),
                product.getPotIncluded(),
                product.getDescription(),
                9000,
                product.getStock(),
                product.getStatus()
        );

        entityManager.flush();
        entityManager.clear();

        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        OrderItem foundOrderItem = orderItemRepository.findByOrder(foundOrder).get(0);

        assertThat(foundOrder.getTotalPrice()).isEqualTo(10000);
        assertThat(foundOrderItem.getProductNameSnapshot()).isEqualTo("주문 당시 상품명");
        assertThat(foundOrderItem.getOrderPrice()).isEqualTo(5000);
        assertThat(foundOrderItem.calculateTotalPrice()).isEqualTo(10000);
        assertThat(foundOrderItem.getProduct().getName()).isEqualTo("변경된 상품명");
        assertThat(foundOrderItem.getProduct().getPrice()).isEqualTo(9000);
    }

    @Test
    @DisplayName("주문 취소 시 주문 상태가 취소로 변경되고 재고가 복구된다")
    void 주문_취소_테스트() {
        // given
        String email = "cancelservice" + System.currentTimeMillis() + "@test.com";

        Member member = memberRepository.save(
                new Member(
                        email,
                        "1234",
                        "010-2222-3333",
                        Role.USER
                )
        );

        Product product = productRepository.save(
                new Product(
                        "방울복랑금",
                        "다육식물",
                        CareLevel.NORMAL,
                        LightRequirement.MEDIUM,
                        WateringCycle.WEEKLY,
                        "https:111,111",
                        "화분 포함",
                        "부분 부분 금색 빛이 도는 식물",
                        5000,
                        10,
                        ProductStatus.ON_SALE
                )
        );

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                product.getId(),
                2,
                "신정범",
                "010-2222-3333",
                "서울시 서초구"
        );

        orderService.createOrder(member.getId(), requestDto);

        Order savedOrder = orderRepository.findByMember(member).get(0);

        // when
        orderService.cancelOrder(member.getId(), savedOrder.getId());

        // then
        Order cancelledOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        Product restoredProduct = productRepository.findById(product.getId()).orElseThrow();

        //상태 변경 확인
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        //재고 복구 확인
        assertThat(restoredProduct.getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("본인의 주문이 아니면 주문을 취소할 수 없다")
    void 본인_주문이_아니면_취소_실패_테스트() {
        // given
        Member orderMember = memberRepository.save(
                new Member(
                        "orderowner" + System.currentTimeMillis() + "@test.com",
                        "1234",
                        "010-3333-4444",
                        Role.USER
                )
        );

        Member otherMember = memberRepository.save(
                new Member(
                        "othermember" + System.currentTimeMillis() + "@test.com",
                        "1234",
                        "010-5555-6666",
                        Role.USER
                )
        );

        Product product = productRepository.save(
                new Product(
                        "방울복랑금",
                        "다육식물",
                        CareLevel.NORMAL,
                        LightRequirement.MEDIUM,
                        WateringCycle.WEEKLY,
                        "https:111,111",
                        "화분 포함",
                        "부분 부분 금색 빛이 도는 식물",
                        5000,
                        10,
                        ProductStatus.ON_SALE
                )
        );

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                product.getId(),
                2,
                "신정범",
                "010-3333-4444",
                "서울시 강남구"
        );

        orderService.createOrder(orderMember.getId(), requestDto);
        Order savedOrder = orderRepository.findByMember(orderMember).get(0);

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(otherMember.getId(), savedOrder.getId()))
                .isInstanceOf(OrderAccessDeniedException.class);

        Product foundProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(foundProduct.getStock()).isEqualTo(8);
    }

    @Test
    @DisplayName("이미 취소된 주문은 다시 취소할 수 없다")
    void 이미_취소된_주문_재취소_실패_테스트() {
        // given
        Member member = memberRepository.save(
                new Member(
                        "alreadycancel" + System.currentTimeMillis() + "@test.com",
                        "1234",
                        "010-7777-8888",
                        Role.USER
                )
        );

        Product product = productRepository.save(
                new Product(
                        "방울복랑금",
                        "다육식물",
                        CareLevel.NORMAL,
                        LightRequirement.MEDIUM,
                        WateringCycle.WEEKLY,
                        "https:111,111",
                        "화분 포함",
                        "부분 부분 금색 빛이 도는 식물",
                        5000,
                        10,
                        ProductStatus.ON_SALE
                )
        );

        OrderCreateRequestDto requestDto = new OrderCreateRequestDto(
                product.getId(),
                2,
                "신정범",
                "010-7777-8888",
                "서울시 강남구"
        );

        orderService.createOrder(member.getId(), requestDto);
        Order savedOrder = orderRepository.findByMember(member).get(0);
        orderService.cancelOrder(member.getId(), savedOrder.getId());

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(member.getId(), savedOrder.getId()))
                .isInstanceOf(OrderAlreadyCancelledException.class);

        Product foundProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(foundProduct.getStock()).isEqualTo(10);
    }
}
