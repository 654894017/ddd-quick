package com.damon.demo.domain.gateway.inventory;

import com.damon.demo.common.exception.TechnicalException;

import java.util.Set;

public class GoodsSoldoutException extends TechnicalException {
    private Set<Long> soldoutGoodsIds;

    public GoodsSoldoutException(String message, Set<Long> soldoutGoodsIds) {
        super(message, null, false, false);
        this.soldoutGoodsIds = soldoutGoodsIds;
    }

    public Set<Long> getSoldoutGoodsIds() {
        return soldoutGoodsIds;
    }
}
