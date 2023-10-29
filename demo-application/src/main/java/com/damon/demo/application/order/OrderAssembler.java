package com.damon.demo.application.order;

import com.damon.demo.client.api.order.dto.OrderSubmitCmd;
import com.damon.demo.common.exception.BusinessException;
import com.damon.demo.domain.gateway.id.IIDGateway;
import com.damon.demo.domain.gateway.customer.ConsigneeInfoDTO;
import com.damon.demo.domain.gateway.customer.ICustomerGateway;
import com.damon.demo.domain.gateway.goods.GoodsBasicInfoDTO;
import com.damon.demo.domain.gateway.goods.IGoodsGateway;
import com.damon.demo.domain.order.entity.Consignee;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderAssembler {

    private final IGoodsGateway goodsGateway;

    private final ICustomerGateway customerGateway;

    private final IIDGateway idGateway;

    public Order assembler(OrderSubmitCmd cmd) {
        Long orderId = idGateway.nextId();
        Set<Long> goodsIds = cmd.getGoods().stream().map(OrderSubmitCmd.Goods::getGoodsId).collect(Collectors.toSet());
        List<GoodsBasicInfoDTO> goodsBasicInfoList = goodsGateway.queryGoodsBasicInfo(goodsIds);
        Map<Long, GoodsBasicInfoDTO> goodsMap = goodsBasicInfoList.stream().collect(
                Collectors.toMap(GoodsBasicInfoDTO::getId, Function.identity())
        );
        if (goodsIds.size() != goodsMap.size()) {
            throw new BusinessException("存在无效的商品信息");
        }
        ConsigneeInfoDTO consigneeInfo = customerGateway.getConsigneeInfo(cmd.getCustomerId(), cmd.getConsigneeId());
        if (consigneeInfo == null) {
            throw new BusinessException("找不到对应的收货人信");
        }
        Order order = new Order();
        order.setId(orderId);
        order.setCouponId(cmd.getCouponId());
        order.setDeductionPoints(cmd.getDeductionPoints());
        order.setCreateTime(System.currentTimeMillis());
        order.setUpdateTime(System.currentTimeMillis());
        order.setVersion(0);
        order.setStatus(0);
        order.setConsignee(new Consignee(consigneeInfo.getName(), consigneeInfo.getShippingAddress(), consigneeInfo.getMobile()));
        List<OrderItem> orderItems = cmd.getGoods().stream().map(goods -> {
            GoodsBasicInfoDTO goodsBasicInfo = goodsMap.get(goods.getGoodsId());
            Long orderItemId = idGateway.nextId();
            OrderItem orderItem = new OrderItem(orderItemId, orderId, goods.getGoodsId(),
                    goodsBasicInfo.getName(), goods.getAmount(), goodsBasicInfo.getPrice()
            );
            return orderItem;
        }).collect(Collectors.toList());
        order.setOrderItems(orderItems);
        return order;
    }

}
