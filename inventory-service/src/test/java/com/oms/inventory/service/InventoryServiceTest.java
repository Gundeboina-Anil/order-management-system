package com.oms.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oms.inventory.event.InventoryUpdatedEvent;
import com.oms.inventory.event.OrderCreatedEvent;
import com.oms.inventory.messaging.InventoryEventPublisher;
import com.oms.inventory.model.InventoryItem;
import com.oms.inventory.model.ProcessedOrder;
import com.oms.inventory.repository.InventoryItemRepository;
import com.oms.inventory.repository.ProcessedOrderRepository;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest
{
    @Mock
    private InventoryItemRepository itemRepository;

    @Mock
    private ProcessedOrderRepository processedOrderRepository;

    @Mock
    private InventoryEventPublisher eventPublisher;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void confirmsOrderAndReducesStockWhenEnoughStock()
    {
        InventoryItem laptop = new InventoryItem(1L, "Laptop", 10);
        when(processedOrderRepository.existsById(100L)).thenReturn(false);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(laptop));

        inventoryService.processOrder(new OrderCreatedEvent(100L, 1L, 3, "anil@example.com"));

        ArgumentCaptor<InventoryUpdatedEvent> captor = ArgumentCaptor.forClass(InventoryUpdatedEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertEquals("CONFIRMED", captor.getValue().status());
        assertEquals(7, laptop.getQuantity());
        verify(processedOrderRepository).save(any(ProcessedOrder.class));
    }

    @Test
    void rejectsOrderWhenStockIsNotEnough()
    {
        InventoryItem laptop = new InventoryItem(1L, "Laptop", 2);
        when(processedOrderRepository.existsById(101L)).thenReturn(false);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(laptop));

        inventoryService.processOrder(new OrderCreatedEvent(101L, 1L, 5, "anil@example.com"));

        ArgumentCaptor<InventoryUpdatedEvent> captor = ArgumentCaptor.forClass(InventoryUpdatedEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertEquals("REJECTED", captor.getValue().status());
        assertEquals("Insufficient stock", captor.getValue().reason());
        assertEquals(2, laptop.getQuantity());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void rejectsOrderWhenProductDoesNotExist()
    {
        when(processedOrderRepository.existsById(102L)).thenReturn(false);
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        inventoryService.processOrder(new OrderCreatedEvent(102L, 99L, 1, "anil@example.com"));

        ArgumentCaptor<InventoryUpdatedEvent> captor = ArgumentCaptor.forClass(InventoryUpdatedEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertEquals("REJECTED", captor.getValue().status());
        assertEquals("Product not found", captor.getValue().reason());
    }

    @Test
    void ignoresDuplicateEvent()
    {
        when(processedOrderRepository.existsById(100L)).thenReturn(true);

        inventoryService.processOrder(new OrderCreatedEvent(100L, 1L, 3, "anil@example.com"));

        verify(itemRepository, never()).findById(any());
        verify(eventPublisher, never()).publish(any());
    }
}
