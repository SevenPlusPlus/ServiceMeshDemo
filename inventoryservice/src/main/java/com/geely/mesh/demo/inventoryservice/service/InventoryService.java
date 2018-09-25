package com.geely.mesh.demo.inventoryservice.service;

import com.geely.mesh.demo.inventoryservice.domain.Inventory;

import java.util.List;

public interface InventoryService {
    Long createProduct(Inventory inventory);
    List<Inventory> getAllProducts();
    Inventory getProductById(Long productId);
    Long calcProductTotalFee(Long productId, Long num);
    Long decInventory(Long productId, Long num);
    Long incInventory(Long productId, Long num);
}
