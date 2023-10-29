package com.damon.demo.domain.gateway.inventory;

import com.damon.demo.common.exception.TechnicalException;

import java.util.Set;

public class InventoryDeficiencyException extends TechnicalException {
    private Set<Long> inventoryDeficiencyGoodsIds;

    public InventoryDeficiencyException(String message, Set<Long> inventoryDeficiencyGoodsIds) {
        super(message, null, false, false);
        this.inventoryDeficiencyGoodsIds = inventoryDeficiencyGoodsIds;
    }
}
