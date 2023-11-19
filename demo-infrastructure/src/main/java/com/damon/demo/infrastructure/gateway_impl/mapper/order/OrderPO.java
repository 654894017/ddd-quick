package com.damon.demo.infrastructure.gateway_impl.mapper.order;


import com.baomidou.mybatisplus.annotation.TableName;
import com.damon.object_trace.Version;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@TableName("demo_order")
public class OrderPO implements Version {

    private int version;
    private Long id;
    private Integer status;
    private Long createTime;
    private Long updateTime;
    private String consigneeName;
    private String consigneeShippingAddress;
    private String consigneeMobile;
    private Long totalMoney;
    private Long actualPayMoney;
    private Long couponId;
    private Long deductionPoints;
    private Long orderSubmitUserId;
    private int isDelete;
    private Long sellerId;
}
