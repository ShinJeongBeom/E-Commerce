package com.jeongbeom.ecommerce.seller.dto;

import com.jeongbeom.ecommerce.order.entity.Order;
import lombok.Getter;

@Getter
public class SellerOrderResponse {
    private final Long id;
    private final String orderNumber;
    private final int totalPrice;
    private final String status;
    private final String receiverName;
    private final String receiverPhone;
    private final String shippingAddress;

    public SellerOrderResponse(Order order) {
        this(order, order.getTotalPrice());
    }

    public SellerOrderResponse(Order order, int sellerTotalPrice) {
        this.id = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.totalPrice = sellerTotalPrice;
        this.status = order.getStatus().name();
        this.receiverName = order.getName();
        this.receiverPhone = order.getPhone();
        this.shippingAddress = order.getAddress();
    }
}
