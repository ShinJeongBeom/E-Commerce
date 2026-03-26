package com.jeongbeom.ecommerce.order.entity;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.entity.repository.OrderItemRepository;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.ProductStatus;
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
        Member member = memberRepository.save(
                new Member("orderitemtest@test.com", "1234", "010-1111-2222", Role.USER)
        );

        Product product = productRepository.save(
                new Product("키보드", "기계식 키보드", 100000, 10, ProductStatus.ON_SALE)
        );

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
                product,
                order,
                1,
                100000
        );

        // when
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // then
        OrderItem foundOrderItem = orderItemRepository.findById(savedOrderItem.getId())
                .orElseThrow();

        System.out.println(foundOrderItem.getProduct().getName());
        System.out.println(foundOrderItem.getOrderQuantity());
        System.out.println(foundOrderItem.getOrderPrice());


    }
}
