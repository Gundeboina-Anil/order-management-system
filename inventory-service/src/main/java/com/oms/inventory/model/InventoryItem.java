package com.oms.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "inventory_items")
public class InventoryItem
{
    @Id
    private Long productId;
    private String name;
    private int quantity;
    @Version
    private Long version;
    protected InventoryItem()
    {
    }
    public InventoryItem(Long productId, String name, int quantity)
    {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
    }
    public Long getProductId()
    {
        return productId;
    }
    public String getName()
    {
        return name;
    }
    public int getQuantity()
    {
        return quantity;
    }
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }
}
