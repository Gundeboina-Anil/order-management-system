package com.oms.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.oms.order.config.KafkaConfig;
import com.oms.order.event.InventoryUpdatedEvent;
import com.oms.order.model.OrderStatus;
import com.oms.order.service.OrderService;

@Component
public class InventoryResultListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryResultListener.class);

    private final OrderService orderService;

    public InventoryResultListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = KafkaConfig.INVENTORY_UPDATED_TOPIC, groupId = "order-group")
    public void onInventoryUpdated(InventoryUpdatedEvent event) {
        log.info("Received inventory result for order {}: {}", event.orderId(), event.status());
        orderService.updateStatus(event.orderId(), OrderStatus.valueOf(event.status()));
    }
}
