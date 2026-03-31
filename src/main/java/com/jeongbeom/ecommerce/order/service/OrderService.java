package com.jeongbeom.ecommerce.order.service;

import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.dto.OrderCreateRequestDto;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderItem;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.order.entity.repository.OrderItemRepository;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    //주문 생성
    public void createOrder(OrderCreateRequestDto orderCreateRequestDto){

        //회원 조회
        Member member = memberRepository.findById(orderCreateRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));
        //상품 조회
        Product product = productRepository.findById(orderCreateRequestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        //재고 차감
        product.decreaseStock(orderCreateRequestDto.getQuantity());

        //총 주문 금액 계산
        int totalPrice = product.getPrice() * orderCreateRequestDto.getQuantity();

        //주문 생성
        Order order = new Order(
                member,
                "ORD-" + System.currentTimeMillis(),
                totalPrice,
                OrderStatus.CREATED,
                orderCreateRequestDto.getName(),
                orderCreateRequestDto.getPhone(),
                orderCreateRequestDto.getAddress()
        );

        //주문 저장
        Order savedOrder = orderRepository.save(order);

        //주문 상품 생성
        OrderItem orderItem = new OrderItem(
                savedOrder,
                product,
                orderCreateRequestDto.getQuantity(),
                product.getPrice()
        );

        //주문 상품 저장
        orderItemRepository.save(orderItem);
    }

    //주문 취소
    public void cancelOrder(Long orderId){
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("주문이 존재하지 않습니다."));

        // 주문 상품 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        // 재고 복구
        for(OrderItem orderItem : orderItems){
            Product product = orderItem.getProduct();
            product.increaseStock(orderItem.getOrderQuantity());
        }

        // 주문 상태 변경
        order.changeStatus(OrderStatus.CANCELLED);

    }
    //주문한 멤버 Id로 주문 목록 조회
    @Transactional(readOnly = true)
    public List<Order> getOrdersByMemberId(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. "));

        return orderRepository.findByMember(member);
    }

}
