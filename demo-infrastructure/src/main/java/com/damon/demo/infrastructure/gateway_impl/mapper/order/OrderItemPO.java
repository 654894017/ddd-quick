package com.damon.demo.infrastructure.gateway_impl.mapper.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.damon.object_trace.ID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("demo_order_item")
public class OrderItemPO implements ID {

    private Long id;
    private Long orderId;
    private Long goodsId;
    private String goodsName;
    private Integer amount;
    private Long price;

}
