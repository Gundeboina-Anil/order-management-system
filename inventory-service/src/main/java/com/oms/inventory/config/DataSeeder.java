package com.oms.inventory.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.oms.inventory.model.InventoryItem;
import com.oms.inventory.repository.InventoryItemRepository;


@Component
public class DataSeeder implements CommandLineRunner
{

    private final InventoryItemRepository repository;

    public DataSeeder(InventoryItemRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public void run(String... args)
    {
        if (repository.count() == 0)
        {
            repository.save(new InventoryItem(1L, "Laptop", 50));
            repository.save(new InventoryItem(2L, "Mobile Phone", 100));
            repository.save(new InventoryItem(3L, "Headphones", 200));
        }
    }
}
