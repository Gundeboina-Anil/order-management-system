package com.oms.inventory.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oms.inventory.event.InventoryUpdatedEvent;
import com.oms.inventory.event.OrderCreatedEvent;
import com.oms.inventory.messaging.InventoryEventPublisher;
import com.oms.inventory.model.InventoryItem;
import com.oms.inventory.model.ProcessedOrder;
import com.oms.inventory.repository.InventoryItemRepository;
import com.oms.inventory.repository.ProcessedOrderRepository;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryItemRepository itemRepository;
    private final ProcessedOrderRepository processedOrderRepository;
    private final InventoryEventPublisher eventPublisher;

    public InventoryService(InventoryItemRepository itemRepository,
                            ProcessedOrderRepository processedOrderRepository,
                            InventoryEventPublisher eventPublisher) {
        this.itemRepository = itemRepository;
        this.processedOrderRepository = processedOrderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void processOrder(OrderCreatedEvent event) {
        // Kafka delivers at-least-once, so the same event can arrive twice.
        if (processedOrderRepository.existsById(event.orderId())) {
            log.warn("Order {} was already processed, ignoring duplicate event", event.orderId());
            return;
        }

        String status;
        String reason;

        Optional<InventoryItem> found = itemRepository.findById(event.productId());
        if (found.isEmpty()) {
            status = "REJECTED";
            reason = "Product not found";
        } else if (found.get().getQuantity() < event.quantity()) {
            status = "REJECTED";
            reason = "Insufficient stock";
        } else {
            InventoryItem item = found.get();
            item.setQuantity(item.getQuantity() - event.quantity());
            itemRepository.save(item);
            status = "CONFIRMED";
            reason = "Stock reserved";
        }

        processedOrderRepository.save(new ProcessedOrder(event.orderId()));

        eventPublisher.publish(new InventoryUpdatedEvent(
                event.orderId(), event.productId(), status, reason, event.customerEmail()));
    }

    public List<InventoryItem> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<InventoryItem> getItem(Long productId) {
        return itemRepository.findById(productId);
    }
}
