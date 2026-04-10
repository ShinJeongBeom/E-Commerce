package com.jeongbeom.ecommerce.cart.entity;

import com.jeongbeom.ecommerce.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    // cascade = CascadeType.ALL 장바구니 삭제시 아이템도 같이 삭제,
    // orphanRemoval = true 리스트에 제거하면 DB도 같이 삭제.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart(Member member){
        this.member = member;
    }

    public void addCartItem(CartItem cartItem){
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    public void removeCartItem(CartItem cartItem){
        this.cartItems.remove(cartItem);
        cartItem.setCart(null);
    }



}
