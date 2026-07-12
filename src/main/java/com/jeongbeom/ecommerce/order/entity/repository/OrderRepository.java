package com.jeongbeom.ecommerce.order.entity.repository;

import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.order.entity.Order;
import com.jeongbeom.ecommerce.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByMember(Member member);

    Optional<Order> findByOrderNumber(String orderNumber);

    long countByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    long countByStatusIn(List<OrderStatus> statuses);

    @Query("select coalesce(sum(o.totalPrice), 0) from Order o where o.status in :statuses")
    long sumTotalPriceByStatusIn(@Param("statuses") List<OrderStatus> statuses);

}
