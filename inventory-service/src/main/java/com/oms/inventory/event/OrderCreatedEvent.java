package com.oms.inventory.event;

public record OrderCreatedEvent(Long orderId, Long productId, int quantity, String customerEmail)
{
}
