package com.oms.inventory.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.oms.inventory.dto.InventoryResponse;
import com.oms.inventory.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<InventoryResponse> getAll() {
        return inventoryService.getAllItems().stream().map(InventoryResponse::from).toList();
    }

    @GetMapping("/{productId}")
    public InventoryResponse getOne(@PathVariable Long productId) {
        return inventoryService.getItem(productId)
                .map(InventoryResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id " + productId));
    }
}
