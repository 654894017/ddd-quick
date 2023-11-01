package com.damon.demo.domain.gateway.inventory;

import com.damon.demo.domain.exception.BusinessException;

import java.util.Set;

public class InventoryDeficiencyException extends BusinessException {
    private Set<Long> inventoryDeficiencyGoodsIds;

    public InventoryDeficiencyException(String message, Set<Long> inventoryDeficiencyGoodsIds) {
        super(message, null, false, false);
        this.inventoryDeficiencyGoodsIds = inventoryDeficiencyGoodsIds;
    }
}
