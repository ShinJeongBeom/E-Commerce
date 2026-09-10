package com.jeongbeom.ecommerce.order.entity;

import com.jeongbeom.ecommerce.common.entity.Role;
import com.jeongbeom.ecommerce.member.entity.Member;
import com.jeongbeom.ecommerce.order.exception.InvalidOrderStatusTransitionException;
import com.jeongbeom.ecommerce.order.exception.OrderAlreadyCancelledException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("주문 상태는 정의된 순서대로 변경된다")
    void orderStatusChangesInDefinedOrder() {
        Order order = createOrder();

        order.changeStatus(OrderStatus.PAID);
        order.changeStatus(OrderStatus.PREPARING);
        order.changeStatus(OrderStatus.SHIPPED);
        order.changeStatus(OrderStatus.DELIVERED);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("정의되지 않은 주문 상태 변경은 거부한다")
    void rejectsUndefinedOrderStatusTransition() {
        Order order = createOrder();

        assertThatThrownBy(() -> order.changeStatus(OrderStatus.SHIPPED))
                .isInstanceOf(InvalidOrderStatusTransitionException.class);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    @DisplayName("결제된 주문은 단순 주문 취소로 변경할 수 없다")
    void paidOrderCannotBeCancelledWithoutRefundFlow() {
        Order order = createOrder();
        order.changeStatus(OrderStatus.PAID);

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStatusTransitionException.class);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("취소된 주문을 다시 취소할 수 없다")
    void cancelledOrderCannotBeCancelledAgain() {
        Order order = createOrder();
        order.cancel();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderAlreadyCancelledException.class);
    }

    private Order createOrder() {
        Member member = new Member("order@test.com", "password", "010-0000-0000", Role.USER);
        return new Order(
                member,
                "ORD-test",
                10000,
                OrderStatus.CREATED,
                "구매자",
                "010-0000-0000",
                "서울시 강남구"
        );
    }
}
