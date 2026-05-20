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
import com.jeongbeom.ecommerce.order.service.OrderService;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.CareLevel;
import com.jeongbeom.ecommerce.product.entity.LightRequirement;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.WateringCycle;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        orderService.cancelOrder(savedOrder.getId());

        // then
        Order cancelledOrder = orderRepository.findById(savedOrder.getId()).orElseThrow();
        Product restoredProduct = productRepository.findById(product.getId()).orElseThrow();

        //상태 변경 확인
        assertThat(cancelledOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        //재고 복구 확인
        assertThat(restoredProduct.getStock()).isEqualTo(10);
    }
}
