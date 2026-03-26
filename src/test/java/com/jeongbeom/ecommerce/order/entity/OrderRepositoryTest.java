package com.jeongbeom.ecommerce.order.entity;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 주문_저장_테스트(){

        // 1. 회원 생성
        Member member = new Member(
                "test@test.com",
                "1234",
                "010-1234-1234",
                Role.USER
        );

        memberRepository.save(member);

        // 2. 주문 생성
        Order order = new Order(
                member,
                "ORD-001",
                10000,
                OrderStatus.CREATED,
                "홍길동",
                "010-1111-2222",
                "서울시 강남구"
        );

        orderRepository.save(order);

        // 3. 검증
        System.out.println("order id = " + order.getId());
        System.out.println("member = " + order.getMember().getEmail());
    }
}

