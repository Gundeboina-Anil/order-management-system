package com.oms.notification.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.oms.notification.event.InventoryUpdatedEvent;

class NotificationServiceTest {

    private final NotificationService service = new NotificationService();

    @Test
    void confirmedOrderMessageSaysConfirmed() {
        var event = new InventoryUpdatedEvent(7L, 1L, "CONFIRMED", "Stock reserved", "anil@example.com");

        assertTrue(service.buildMessage(event).contains("confirmed"));
    }

    @Test
    void rejectedOrderMessageContainsReason() {
        var event = new InventoryUpdatedEvent(8L, 1L, "REJECTED", "Insufficient stock", "anil@example.com");

        assertTrue(service.buildMessage(event).contains("Insufficient stock"));
    }
}
