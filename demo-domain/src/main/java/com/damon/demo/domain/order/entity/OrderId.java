package com.damon.demo.domain.order.entity;

import lombok.Data;

@Data
public class OrderId {

    public Long id;

    public OrderId(Long id) {
        this.id = id;
    }
}
