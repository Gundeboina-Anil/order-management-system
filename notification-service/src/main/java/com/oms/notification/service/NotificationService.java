package com.oms.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.oms.notification.event.InventoryUpdatedEvent;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    /** Builds the message that would be emailed. Sending is only simulated with a log line. */
    public String buildMessage(InventoryUpdatedEvent event) {
        if ("CONFIRMED".equals(event.status())) {
            return "Hi, your order #" + event.orderId() + " is confirmed.";
        }
        return "Hi, sorry, your order #" + event.orderId() + " could not be placed: " + event.reason();
    }

    public void send(InventoryUpdatedEvent event) {
        log.info("[EMAIL to {}] {}", event.customerEmail(), buildMessage(event));
    }
}
