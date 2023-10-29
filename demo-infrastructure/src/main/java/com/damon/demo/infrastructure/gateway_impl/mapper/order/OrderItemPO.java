package com.damon.demo.infrastructure.gateway_impl.mapper.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemPO {

    private Long id;
    private Long orderId;
    private Long goodsId;
    private String goodsName;
    private Integer amount;
    private Long price;

}
