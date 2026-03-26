package com.jeongbeom.ecommerce.order.entity.repository;

import com.jeongbeom.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
