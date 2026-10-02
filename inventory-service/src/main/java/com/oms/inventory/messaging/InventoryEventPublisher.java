package com.oms.inventory.messaging;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.oms.inventory.config.KafkaConfig;
import com.oms.inventory.event.InventoryUpdatedEvent;

@Component
public class InventoryEventPublisher
{

    private static final Logger log = LoggerFactory.getLogger(InventoryEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryEventPublisher(KafkaTemplate<String, Object> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(InventoryUpdatedEvent event)
    {
        try
        {
            kafkaTemplate.send(KafkaConfig.INVENTORY_UPDATED_TOPIC, String.valueOf(event.orderId()), event)
                    .get(5, TimeUnit.SECONDS);
            log.info("Published inventory-updated event for order {}: {}", event.orderId(), event.status());
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing inventory-updated event", e);
        }
        catch (ExecutionException | TimeoutException e)
        {
            throw new IllegalStateException("Could not publish inventory-updated event", e);
        }
    }
}
