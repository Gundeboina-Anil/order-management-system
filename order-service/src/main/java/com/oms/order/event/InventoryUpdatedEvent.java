package com.oms.order.event;

public record InventoryUpdatedEvent(Long orderId, Long productId, String status, String reason, String customerEmail) {
}
