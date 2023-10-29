package com.damon.demo.infrastructure.gateway_impl.order;


import com.damon.demo.domain.order.entity.Consignee;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderItem;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderItemPO;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderPO;
import com.damon.object_trace.Aggregate;
import com.damon.object_trace.AggregateFactory;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

public class OrderFactory {
    public static OrderPO convert(@NonNull Order order) {
        OrderPO orderPO = OrderPO.builder()
                .id(order.getId())
                .consigneeShippingAddress(order.getConsignee().getShippingAddress())
                .actualPayMoney(order.getActualPayMoney())
                .consigneeMobile(order.getConsignee().getMobile())
                .consigneeName(order.getConsignee().getName())
                .totalMoney(order.getTotalMoney())
                .delete(order.getDelete())
                .status(order.getStatus())
                .version(order.getVersion())
                .updateTime(order.getUpdateTime())
                .createTime(order.getCreateTime())
                .deductionPoints(order.getDeductionPoints())
                .couponId(order.getCouponId())
                .orderSubmitUserId(order.getOrderSubmitUserId())
                .sellerId(order.getSellerId())
                .build();
        return orderPO;
    }

    public static List<OrderItemPO> convert(@NonNull List<OrderItem> itemList) {
        return itemList.stream().map(item -> {
            return OrderItemPO.builder().orderId(item.getOrderId()).goodsName(item.getGoodsName()).goodsId(item.getGoodsId())
                    .id(item.getId()).price(item.getPrice()).amount(item.getAmount()).build();
        }).collect(Collectors.toList());
    }

    public static OrderItemPO convert(@NonNull OrderItem item) {
        return OrderItemPO.builder().orderId(item.getOrderId()).goodsName(item.getGoodsName())
                .id(item.getId()).price(item.getPrice()).amount(item.getAmount()).build();
    }

    public static Aggregate<Order> convert(@NonNull OrderPO orderPO, @NonNull List<OrderItemPO> orderItemPOS) {
        Order order = new Order();
        List<OrderItem> orderItems = orderItemPOS.stream().map(item -> {
            return new OrderItem(item.getId(), item.getOrderId(), item.getGoodsId(), item.getGoodsName(), item.getAmount(), item.getPrice());
        }).collect(Collectors.toList());
        order.setId(orderPO.getId());
        order.setConsignee(new Consignee(orderPO.getConsigneeName(), orderPO.getConsigneeShippingAddress(), orderPO.getConsigneeMobile()));
        order.setVersion(orderPO.getVersion());
        order.setStatus(orderPO.getStatus());
        order.setUpdateTime(orderPO.getUpdateTime());
        order.setCreateTime(orderPO.getCreateTime());
        order.setDeductionPoints(orderPO.getDeductionPoints());
        order.setCouponId(orderPO.getCouponId());
        order.setActualPayMoney(orderPO.getActualPayMoney());
        order.setTotalMoney(orderPO.getTotalMoney());
        order.setOrderSubmitUserId(orderPO.getOrderSubmitUserId());
        order.setDelete(orderPO.getDelete());
        order.setOrderItems(orderItems);
        order.setSellerId(orderPO.getSellerId());
        return AggregateFactory.createAggregate(order);
    }
}
