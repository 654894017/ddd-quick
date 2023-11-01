package com.damon.demo.domain.gateway.inventory;

import com.damon.demo.domain.exception.BusinessException;

import java.util.Set;

public class GoodsSoldoutException extends BusinessException {
    private Set<Long> soldoutGoodsIds;

    public GoodsSoldoutException(String message, Set<Long> soldoutGoodsIds) {
        super(message, null, false, false);
        this.soldoutGoodsIds = soldoutGoodsIds;
    }

    public Set<Long> getSoldoutGoodsIds() {
        return soldoutGoodsIds;
    }
}
