package com.oms.inventory.dto;

import com.oms.inventory.model.InventoryItem;

public record InventoryResponse(Long productId, String name, int quantity)
{

    public static InventoryResponse from(InventoryItem item)
    {
        return new InventoryResponse(item.getProductId(), item.getName(), item.getQuantity());
    }
}
