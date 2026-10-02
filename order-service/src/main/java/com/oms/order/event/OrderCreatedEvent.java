package com.oms.order.event;

public record OrderCreatedEvent(Long orderId, Long productId, int quantity, String customerEmail) {
}
