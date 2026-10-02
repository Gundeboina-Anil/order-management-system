package com.oms.order.dto;

import java.time.LocalDateTime;

import com.oms.order.model.Order;
import com.oms.order.model.OrderStatus;

public record OrderResponse(
        Long id,
        Long productId,
        int quantity,
        String customerEmail,
        OrderStatus status,
        LocalDateTime createdAt) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getCreatedAt());
    }
}
