package com.oms.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oms.inventory.model.ProcessedOrder;

public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder, Long>
{
}
