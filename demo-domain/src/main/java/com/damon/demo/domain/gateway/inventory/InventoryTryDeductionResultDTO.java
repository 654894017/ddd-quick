package com.damon.demo.domain.gateway.inventory;

import lombok.Data;

@Data
public class InventoryTryDeductionResultDTO {


    private Integer status;

    private Long inventoryScarceGoodsId;

    private Long soldoutGoodsId;

    public InventoryTryDeductionResultDTO(Integer status) {
        this.status = status;
    }

    public InventoryTryDeductionResultDTO(Integer status, Long inventoryScarceGoodsId) {
        this.status = status;
        this.inventoryScarceGoodsId = inventoryScarceGoodsId;
    }

    public InventoryTryDeductionResultDTO() {
    }

    public boolean isInventoryScarceGoods() {
        return inventoryScarceGoodsId != null;
    }

    public boolean isSoldoutGoods() {
        return soldoutGoodsId != null;
    }

    public boolean isSucceeded() {
        return status == 1;
    }
}
