package com.oms.order.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.oms.order.config.KafkaConfig;
import com.oms.order.event.OrderCreatedEvent;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        // orderId is the message key, so all events of one order go to the same partition
        kafkaTemplate.send(KafkaConfig.ORDER_CREATED_TOPIC, String.valueOf(event.orderId()), event);
        log.info("Published order-created event for order {}", event.orderId());
    }
}
