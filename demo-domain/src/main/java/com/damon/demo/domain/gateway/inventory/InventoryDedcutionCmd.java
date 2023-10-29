package com.damon.demo.domain.gateway.inventory;

import lombok.Data;

import java.util.Set;

@Data
public class InventoryDedcutionCmd {

    private Long bizId;
    private Set<Item> items;

    public InventoryDedcutionCmd(Long bizId, Set<Item> items) {
        this.bizId = bizId;
        this.items = items;
    }

    public InventoryDedcutionCmd() {
    }

    @Data
    public static class Item {
        private Long goodsId;

        private Integer amount;

        public Item(Long goodsId, Integer amount) {
            this.goodsId = goodsId;
            this.amount = amount;
        }

        public Item() {
        }
    }

}
