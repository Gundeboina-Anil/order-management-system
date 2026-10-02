package com.oms.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oms.inventory.model.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>
{
}
