package com.jeongbeom.ecommerce.order.dto;

import com.jeongbeom.ecommerce.order.entity.Order;
import lombok.Getter;

@Getter
public class OrderResponseDto {

    private Long orderId;
    private String orderNumber;
    private int totalPrice;
    private String status;
    private String name;
    private String phone;
    private String address;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.totalPrice = order.getTotalPrice();
        this.status = String.valueOf(order.getStatus());
        this.name = order.getName();
        this.phone = order.getPhone();
        this.address = order.getAddress();
    }
}
