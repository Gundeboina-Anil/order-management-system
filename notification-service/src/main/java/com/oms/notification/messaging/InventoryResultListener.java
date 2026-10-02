package com.oms.notification.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.oms.notification.config.KafkaConfig;
import com.oms.notification.event.InventoryUpdatedEvent;
import com.oms.notification.service.NotificationService;

@Component
public class InventoryResultListener {

    private final NotificationService notificationService;

    public InventoryResultListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = KafkaConfig.INVENTORY_UPDATED_TOPIC, groupId = "notification-group")
    public void onInventoryUpdated(InventoryUpdatedEvent event) {
        notificationService.send(event);
    }
}
