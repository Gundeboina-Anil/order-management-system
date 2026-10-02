package com.oms.inventory.event;

public record InventoryUpdatedEvent(Long orderId, Long productId, String status, String reason, String customerEmail)
{
}
