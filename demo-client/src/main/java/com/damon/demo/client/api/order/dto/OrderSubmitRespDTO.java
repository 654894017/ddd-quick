package com.damon.demo.client.api.order.dto;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class OrderSubmitRespDTO {
    private Integer orderSubmitStatus;
    private Long orderId;
    private Map<Long, String> inventoryScarceGoods;
    private Map<Long, String> soldoutGoods;

    public OrderSubmitRespDTO(Integer orderSubmitStatus, Long orderId) {
        this.orderSubmitStatus = orderSubmitStatus;
        this.orderId = orderId;
    }

    public OrderSubmitRespDTO(Integer orderSubmitStatus) {
        this.orderSubmitStatus = orderSubmitStatus;
    }

    public OrderSubmitRespDTO(Integer orderSubmitStatus, Long orderId, Map<Long, String> inventoryScarceGoods, Map<Long, String> soldoutGoods) {
        this.orderSubmitStatus = orderSubmitStatus;
        this.orderId = orderId;
        this.inventoryScarceGoods = inventoryScarceGoods;
        this.soldoutGoods = soldoutGoods;
    }

}
