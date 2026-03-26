package com.jeongbeom.ecommerce.order.entity.repository;

import com.jeongbeom.ecommerce.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
