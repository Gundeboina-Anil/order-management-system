package com.oms.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.oms.order.dto.OrderRequest;
import com.oms.order.dto.OrderResponse;
import com.oms.order.event.OrderCreatedEvent;
import com.oms.order.exception.OrderNotFoundException;
import com.oms.order.messaging.OrderEventPublisher;
import com.oms.order.model.Order;
import com.oms.order.model.OrderStatus;
import com.oms.order.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder_savesOrderAsPlacedAndPublishesEvent() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(10L);
            return order;
        });

        OrderResponse response = orderService.placeOrder(new OrderRequest(1L, 2, "anil@example.com"));

        assertEquals(10L, response.id());
        assertEquals(OrderStatus.PLACED, response.status());
        verify(eventPublisher).publishOrderCreated(any(OrderCreatedEvent.class));
    }

    @Test
    void getOrder_throwsWhenOrderDoesNotExist() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(99L));
    }

    @Test
    void updateStatus_changesStatusOfExistingOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.PLACED);
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        orderService.updateStatus(5L, OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }
}
