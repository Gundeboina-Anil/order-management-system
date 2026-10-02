package com.oms.inventory.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.oms.inventory.config.KafkaConfig;
import com.oms.inventory.event.OrderCreatedEvent;
import com.oms.inventory.service.InventoryService;

@Component
public class OrderEventListener
{

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final InventoryService inventoryService;

    public OrderEventListener(InventoryService inventoryService)
    {
        this.inventoryService = inventoryService;
    }

    @KafkaListener(topics = KafkaConfig.ORDER_CREATED_TOPIC, groupId = "inventory-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Received order-created event for order {}", event.orderId());
        inventoryService.processOrder(event);
    }
}
