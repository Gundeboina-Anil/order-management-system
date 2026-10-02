package com.oms.notification.event;

public record InventoryUpdatedEvent(Long orderId, Long productId, String status, String reason, String customerEmail) {
}
