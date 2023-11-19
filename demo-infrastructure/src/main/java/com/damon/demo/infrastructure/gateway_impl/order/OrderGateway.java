package com.damon.demo.infrastructure.gateway_impl.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.damon.demo.domain.order.IOrderGateway;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderId;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderItemMapper;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderItemPO;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderMapper;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderPO;
import com.damon.object_trace.Aggregate;
import com.damon.object_trace.mybatis.MybatisRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderGateway extends MybatisRepositorySupport implements IOrderGateway {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderGateway(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public Aggregate<Order> get(OrderId orderId) {
        OrderPO orderPO = orderMapper.selectById(orderId.getId());
        List<OrderItemPO> orderItemPOList = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemPO>().eq(OrderItemPO::getOrderId, orderId.getId()));
        return OrderFactory.convert(orderPO, orderItemPOList);
    }

    @Override
    public void create(Aggregate<Order> orderAggregate) {
        Order root = orderAggregate.getRoot();
        OrderPO orderPO = OrderFactory.convert(root);
        orderMapper.insert(orderPO);
        root.getOrderItems().forEach(orderItem -> {
            OrderItemPO orderItemPO = OrderFactory.convertPO(orderItem);
            orderItemMapper.insert(orderItemPO);
        });
    }

    @Override
    public void save(Aggregate<Order> orderAggregate) {
        Order root = orderAggregate.getRoot();
        Order snapshot = orderAggregate.getSnapshot();
        super.executeSafeUpdate(root, snapshot, OrderFactory::convert);
        super.executeUpdateList(root.getOrderItems(), snapshot.getOrderItems(), OrderFactory::convertPO);
    }


}
