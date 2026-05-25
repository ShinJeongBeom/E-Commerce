package com.jeongbeom.ecommerce.order.service;

import com.jeongbeom.ecommerce.cart.entity.Cart;
import com.jeongbeom.ecommerce.cart.entity.CartItem;
import com.jeongbeom.ecommerce.cart.entity.repository.CartItemRepository;
import com.jeongbeom.ecommerce.cart.entity.repository.CartRepository;
import com.jeongbeom.ecommerce.cart.exception.CartItemNotFoundException;
import com.jeongbeom.ecommerce.cart.exception.CartNotFoundException;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.order.dto.CartOrderRequest;
import com.jeongbeom.ecommerce.order.dto.OrderCreateRequestDto;
import com.jeongbeom.ecommerce.order.dto.OrderResponseDto;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderItem;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import com.jeongbeom.ecommerce.order.entity.repository.OrderItemRepository;
import com.jeongbeom.ecommerce.order.entity.repository.OrderRepository;
import com.jeongbeom.ecommerce.order.exception.OrderAccessDeniedException;
import com.jeongbeom.ecommerce.order.exception.OrderAlreadyCancelledException;
import com.jeongbeom.ecommerce.order.exception.OrderNotFoundException;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    //주문 생성
    @Transactional
    public void createOrder(Long memberId, OrderCreateRequestDto orderCreateRequestDto){

        //회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        //상품 조회
        Product product = productRepository.findByIdWithLock(orderCreateRequestDto.getProductId())
                .orElseThrow(ProductNotFoundException::new);

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
    @Transactional
    public void cancelOrder(Long memberId, Long orderId){
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getMember().getId().equals(memberId)){
            throw new OrderAccessDeniedException();
        }

        if (order.getStatus() == OrderStatus.CANCELLED){
            throw new OrderAlreadyCancelledException();
        }

        // 주문 상품 조회
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        // 재고 복구
        for(OrderItem orderItem : orderItems){
            Product product = productRepository.findByIdWithLock(orderItem.getProduct().getId())
                    .orElseThrow(ProductNotFoundException::new);
            product.increaseStock(orderItem.getOrderQuantity());
        }

        // 주문 상태 변경
        order.changeStatus(OrderStatus.CANCELLED);

    }
    //주문한 멤버 Id로 주문 목록 조회
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByMemberId(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        return orderRepository.findByMember(member).stream()
                .map(OrderResponseDto::new)
                .toList();
    }

    //장바구니 선택 주문
    @Transactional
    public void createOrderFromCartItems(Long memberId, CartOrderRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        List<CartItem> cartItems = cartItemRepository.findAllById(request.getCartItemIds());

        if (cartItems.size() != request.getCartItemIds().size()) {
            throw new CartItemNotFoundException();
        }

        int totalPrice = 0;

        Order order = new Order(
                member,
                "ORD-" + System.currentTimeMillis(),
                0,
                OrderStatus.CREATED,
                request.getName(),
                request.getPhone(),
                request.getAddress()
        );

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            if (!cartItem.getCart().getMember().getId().equals(memberId)) {
                throw new CartItemNotFoundException();
            }

            Product product = productRepository.findByIdWithLock(cartItem.getProduct().getId())
                    .orElseThrow(ProductNotFoundException::new);

            product.decreaseStock(cartItem.getQuantity());

            int orderPrice = product.getPrice();
            totalPrice += orderPrice * cartItem.getQuantity();

            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    product,
                    cartItem.getQuantity(),
                    orderPrice
            );

            orderItemRepository.save(orderItem);
        }

        savedOrder.changeTotalPrice(totalPrice);

        cartItemRepository.deleteAll(cartItems);
    }

    //전체 장바구니 주문
    @Transactional
    public void createOrderFromCart(Long memberId, CartOrderRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Cart cart = cartRepository.findByMember(member)
                .orElseThrow(CartNotFoundException::new);

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new CartItemNotFoundException();
        }

        CartOrderRequest cartOrderRequest = new CartOrderRequest(
                cartItems.stream()
                        .map(CartItem::getId)
                        .toList(),
                request.getName(),
                request.getPhone(),
                request.getAddress()
        );

        createOrderFromCartItems(memberId, cartOrderRequest);
    }


}
