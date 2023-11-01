package com.damon.demo.infrastructure.gateway_impl.order;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.damon.demo.domain.exception.OptimisticLockException;
import com.damon.demo.domain.order.IOrderGateway;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderId;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderItemMapper;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderItemPO;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderMapper;
import com.damon.demo.infrastructure.gateway_impl.mapper.order.OrderPO;
import com.damon.demo.infrastructure.mybatis.UpdateWrapperNew;
import com.damon.object_trace.Aggregate;
import com.damon.object_trace.ChangedEntity;
import com.damon.object_trace.ObjectComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class OrderGateway implements IOrderGateway {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    @Override
    public Aggregate<Order> get(OrderId orderId) {
        OrderPO orderPO = orderMapper.selectById(orderId.getId());
        List<OrderItemPO> orderItemPOList = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemPO>().eq(OrderItemPO::getOrderId, orderId.getId()));
        return OrderFactory.convert(orderPO, orderItemPOList);
    }

    @Override
    public void save(Aggregate<Order> orderAggregate) {
        if (orderAggregate.isNew()) {
            Order order = orderAggregate.getRoot();
            OrderPO orderPO = OrderFactory.convert(orderAggregate.getRoot());
            orderMapper.insert(orderPO);
            order.getOrderItems().forEach(orderItem -> {
                OrderItemPO orderItemPO = OrderFactory.convert(orderItem);
                orderItemMapper.insert(orderItemPO);
            });
        } else if (orderAggregate.isChanged()) {
            Order order = orderAggregate.getRoot();
            Order orderSnapshot = orderAggregate.getSnapshot();
            OrderPO newOrderPO = OrderFactory.convert(order);
            OrderPO oldOrderPO = OrderFactory.convert(orderSnapshot);
            updateOrder(newOrderPO, oldOrderPO);
            List<OrderItemPO> newItemPOS = OrderFactory.convert(order.getOrderItems());
            List<OrderItemPO> oldItemPOS = OrderFactory.convert(orderSnapshot.getOrderItems());
            insertOrderItem(newItemPOS, oldItemPOS);
            updateOrderItem(newItemPOS, oldItemPOS);
            deleteOrderItem(newItemPOS, oldItemPOS);
        }
    }

    private void updateOrder(OrderPO newOrder, OrderPO oldOrder) {
        UpdateWrapperNew<OrderPO> wrapper = new UpdateWrapperNew<>();
        Set<String> changedFields = ObjectComparator.findChangedFields(newOrder, oldOrder);
        wrapper.set(changedFields, newOrder);
        wrapper.set("version", newOrder.getVersion() + 1);
        wrapper.eq("version", newOrder.getVersion());
        wrapper.eq("id", newOrder.getId());
        int result = orderMapper.update(null, wrapper);
        if (result != 1) {
            throw new OptimisticLockException(String.format("Update order (%s) error, it's not found or changed by another order",
                    newOrder.getId()));
        }
    }

    private void insertOrderItem(List<OrderItemPO> newItemPOS, List<OrderItemPO> oldItemPOS) {
        List<OrderItemPO> orderItemPOS = ObjectComparator.findNewEntities(newItemPOS, oldItemPOS, OrderItemPO::getId);
        for (OrderItemPO item : orderItemPOS) {
            orderItemMapper.insert(item);
        }
    }

    private void updateOrderItem(List<OrderItemPO> newItemPOS, List<OrderItemPO> oldItemPOS) {
        List<ChangedEntity<OrderItemPO>> changedEntityList = ObjectComparator.findChangedEntities(newItemPOS, oldItemPOS, OrderItemPO::getId);
        for (ChangedEntity<OrderItemPO> changedEntity : changedEntityList) {
            Set<String> changedFields = ObjectComparator.findChangedFields(changedEntity.getNewEntity(), changedEntity.getOldEntity());
            if (CollUtil.isNotEmpty(changedFields)) {
                UpdateWrapperNew<OrderItemPO> wrapper = new UpdateWrapperNew<>();
                wrapper.set(changedFields, changedEntity.getNewEntity());
                wrapper.eq("id", changedEntity.getNewEntity().getId());
                int result = orderItemMapper.update(null, wrapper);
                if (result != 1) {
                    throw new OptimisticLockException(String.format("Update order item (%d) error, it's not found", changedEntity.getNewEntity().getId()));
                }
            }
        }
    }

    private void deleteOrderItem(List<OrderItemPO> newItemPOS, List<OrderItemPO> oldItemPOS) {
        List<OrderItemPO> orderItemPOS = ObjectComparator.findRemovedEntities(newItemPOS, oldItemPOS, OrderItemPO::getId);
        for (OrderItemPO item : orderItemPOS) {
            int result = orderItemMapper.deleteById(item.getId());
            if (result != 1) {
                throw new OptimisticLockException(String.format("Delete order item (%d) error, it's not found", item.getId()));
            }
        }
    }

}
