package com.jeongbeom.ecommerce.order.entity.repository;

import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMember(Member member);

    long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    long countByStatusIn(List<OrderStatus> statuses);

}
