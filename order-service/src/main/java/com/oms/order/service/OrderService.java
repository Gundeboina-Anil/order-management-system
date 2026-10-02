package com.oms.order.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oms.order.dto.OrderRequest;
import com.oms.order.dto.OrderResponse;
import com.oms.order.event.OrderCreatedEvent;
import com.oms.order.exception.OrderNotFoundException;
import com.oms.order.messaging.OrderEventPublisher;
import com.oms.order.model.Order;
import com.oms.order.model.OrderStatus;
import com.oms.order.repository.OrderRepository;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    // Not @Transactional on purpose: save() commits before the event is published,
    // so Inventory can never receive an event for an order that is not in the database yet.
    public OrderResponse placeOrder(OrderRequest request) {
        Order order = new Order();
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        order.setCustomerEmail(request.customerEmail());
        order.setStatus(OrderStatus.PLACED);

        Order saved = orderRepository.save(order);

        eventPublisher.publishOrderCreated(new OrderCreatedEvent(
                saved.getId(), saved.getProductId(), saved.getQuantity(), saved.getCustomerEmail()));

        return OrderResponse.from(saved);
    }

    public OrderResponse getOrder(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresentOrElse(
                order -> order.setStatus(status),
                () -> log.warn("Received inventory result for unknown order {}", orderId));
    }
}
