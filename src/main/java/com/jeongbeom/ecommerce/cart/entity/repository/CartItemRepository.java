package com.jeongbeom.ecommerce.cart.entity.repository;

import com.jeongbeom.ecommerce.cart.entity.Cart;
import com.jeongbeom.ecommerce.cart.entity.CartItem;
import com.jeongbeom.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
