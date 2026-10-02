package com.oms.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "processed_orders")
public class ProcessedOrder
{
    @Id
    private Long orderId;
    protected ProcessedOrder()
    {
    }

    public ProcessedOrder(Long orderId)
    {
        this.orderId = orderId;
    }
    public Long getOrderId()
    {
        return orderId;
    }
}
