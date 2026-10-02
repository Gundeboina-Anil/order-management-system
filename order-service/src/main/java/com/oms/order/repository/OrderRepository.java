package com.oms.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oms.order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
