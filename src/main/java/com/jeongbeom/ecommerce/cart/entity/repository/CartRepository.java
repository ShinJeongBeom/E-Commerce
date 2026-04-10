package com.jeongbeom.ecommerce.cart.entity.repository;

import com.jeongbeom.ecommerce.cart.entity.Cart;
import com.jeongbeom.ecommerce.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMember(Member member);

}
