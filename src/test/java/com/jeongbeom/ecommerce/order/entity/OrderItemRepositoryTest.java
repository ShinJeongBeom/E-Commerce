package com.jeongbeom.ecommerce.order.entity;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.entity.repository.OrderItemRepository;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.product.entity.CareLevel;
import com.jeongbeom.ecommerce.product.entity.LightRequirement;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
import com.jeongbeom.ecommerce.product.entity.WateringCycle;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OrderItemRepositoryTest {

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 주문_상품_테스트(){
        // given
        //Member 저장
        Member member = memberRepository.save(
                new Member("orderitemtest@test.com", "1234", "010-1111-2222", Role.USER)
        );
        //product 저장
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
        //Order 저장
        Order order = orderRepository.save(
                new Order(
                        member,
                        "ORD-20260326-0001",
                        100000,
                        OrderStatus.CREATED,
                        "신정범",
                        "010-1111-2222",
                        "서울시 강남구"
                )
        );

        OrderItem orderItem = new OrderItem(
                order,
                product,
                1,
                100000
        );

        // when
        //OrderItem 저장
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // then
        //OrderItem 조회
        OrderItem foundOrderItem = orderItemRepository.findById(savedOrderItem.getId())
                .orElseThrow();

        //Product 이름, 주문수량, 주문 가격이 잘 나오는지 확인
        System.out.println(foundOrderItem.getProduct().getName());
        System.out.println(foundOrderItem.getOrderQuantity());
        System.out.println(foundOrderItem.getOrderPrice());


    }
}
