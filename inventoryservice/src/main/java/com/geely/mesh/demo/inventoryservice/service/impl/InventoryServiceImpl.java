package com.geely.mesh.demo.inventoryservice.service.impl;

import com.geely.mesh.demo.inventoryservice.domain.Inventory;
import com.geely.mesh.demo.inventoryservice.exception.ProductNotAvailableException;
import com.geely.mesh.demo.inventoryservice.exception.ProductNotFoundException;
import com.geely.mesh.demo.inventoryservice.service.InventoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InventoryServiceImpl implements InventoryService {

    static Map<Long, Inventory> inventoryMap = new ConcurrentHashMap<Long, Inventory>();
    static AtomicLong productIdCounter = new AtomicLong(0);

    @Override
    public Long createProduct(Inventory inventory) {
        Long productId = productIdCounter.addAndGet(1);
        inventory.setProductId(productId);
        inventoryMap.put(productId, inventory);
        return null;
    }

    @Override
    public List<Inventory> getAllProducts() {
        return new ArrayList<>(inventoryMap.values());
    }

    private void checkProductExists(Long productId)
    {
        if(!inventoryMap.containsKey(productId))
        {
            throw new ProductNotFoundException(productId);
        }
    }

    @Override
    public Inventory getProductById(Long productId) {
        checkProductExists(productId);
        return inventoryMap.get(productId);
    }

    @Override
    public Long calcProductTotalFee(Long productId, Long num) {
        checkProductExists(productId);
        return inventoryMap.get(productId).getPrice() * num;
    }

    @Override
    public Long decInventory(Long productId, Long num) {
        checkProductExists(productId);
        Long currentAvail = inventoryMap.get(productId).getAvailableNum();
        Long newAvail = currentAvail - num;
        if(newAvail >= 0)
        {
            inventoryMap.get(productId).setAvailableNum(newAvail);
            return newAvail;
        }
        throw new ProductNotAvailableException(productId);
    }

    @Override
    public Long incInventory(Long productId, Long num) {
        checkProductExists(productId);
        Long currentAvail = inventoryMap.get(productId).getAvailableNum();
        Long newAvail = currentAvail + num;
        inventoryMap.get(productId).setAvailableNum(newAvail);
        return newAvail;
    }
}
