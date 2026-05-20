package com.jeongbeom.ecommerce.cart.service;

import com.jeongbeom.ecommerce.cart.dto.AddCartRequestDto;
import com.jeongbeom.ecommerce.cart.dto.CartItemQuantityUpdateRequest;
import com.jeongbeom.ecommerce.cart.dto.CartItemResponseDto;
import com.jeongbeom.ecommerce.cart.entity.Cart;
import com.jeongbeom.ecommerce.cart.entity.CartItem;
import com.jeongbeom.ecommerce.cart.entity.repository.CartItemRepository;
import com.jeongbeom.ecommerce.cart.entity.repository.CartRepository;
import com.jeongbeom.ecommerce.cart.exception.CartItemNotFoundException;
import com.jeongbeom.ecommerce.cart.exception.CartNotFoundException;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.member.exception.MemberNotFoundException;
import com.jeongbeom.ecommerce.member.repository.MemberRepository;
import com.jeongbeom.ecommerce.product.entity.Product;
import com.jeongbeom.ecommerce.product.entity.repository.ProductRepository;
import com.jeongbeom.ecommerce.product.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * 장바구니 상품 추가
     */
    @Transactional
    public void addCartItem(Long memberId, AddCartRequestDto addCartRequestDto) {

        // 회원 기준으로 장바구니 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow((MemberNotFoundException::new));

        // 상품 조회
        Product product = productRepository.findById(addCartRequestDto.getProductId())
                .orElseThrow((ProductNotFoundException::new));

        // 장바구니 조회
        Cart cart = cartRepository.findByMember(member)
                .orElseGet(() -> cartRepository.save(new Cart(member)));

        // 이미 담긴 상품인지 확인
        Optional<CartItem> optionalCartItem =
                cartItemRepository.findByCartAndProduct(cart, product);

        // 분기 처리
        if (optionalCartItem.isPresent()) {
            // 이미 있으면 수량 증가
            CartItem cartItem = optionalCartItem.get();
            cartItem.increaseQuantity(addCartRequestDto.getQuantity());
        } else {
            // 없으면 새로 생성
            CartItem cartItem = new CartItem(product, addCartRequestDto.getQuantity());
            cart.addCartItem(cartItem);
            cartItemRepository.save(cartItem);
        }
    }
    /**
      * 장바구니 조회
      */
    @Transactional(readOnly = true)
    public List<CartItemResponseDto> getCartItems(Long memberId) {

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow((MemberNotFoundException::new));

        // 회원의 장바구니 조회
        Cart cart = cartRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("장바구니 없음"));

        // CartItem조회
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        // DTO 반환
        return cartItems.stream()
                .map(item -> new CartItemResponseDto(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity()
                ))
                        .toList();

    }

    /**
     * 장바구니 상품 삭제
     */
    @Transactional
    public void removeCartItem(Long memberId, Long cartItemId) {

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 장바구니 조회
        Cart cart = cartRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("장바구니 없음"));

        // CartItem 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("장바구니 상품 없음"));

        // 검증 (내 장바구니 맞는지)
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("잘못된 접근");
        }

        // 5. 삭제
        cart.removeCartItem(cartItem);
        cartItemRepository.delete(cartItem);
    }

    //수량 변경
    @Transactional
    public void updateCartItemQuantity(
            Long memberId,
            Long cartItemId,
            CartItemQuantityUpdateRequest request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Cart cart = cartRepository.findByMember(member)
                .orElseThrow(CartNotFoundException::new);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(CartItemNotFoundException::new);

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new CartItemNotFoundException();
        }

        cartItem.changeQuantity(request.getQuantity());
    }


}
