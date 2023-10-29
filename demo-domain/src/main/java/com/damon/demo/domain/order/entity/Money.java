package com.damon.demo.domain.order.entity;

import com.damon.demo.common.domain.ValueObject;
import lombok.Data;
import lombok.NonNull;

@Data
public class Money implements ValueObject {
    /**
     * 币种
     */
    private String currency;
    private Long money;

    public Money(@NonNull String currency, @NonNull Long money) {
        this.currency = currency;
        this.money = money;
        ;
    }
}
