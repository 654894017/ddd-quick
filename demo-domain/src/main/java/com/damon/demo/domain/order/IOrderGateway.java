package com.damon.demo.domain.order;

import com.damon.demo.common.exception.OptimisticLockException;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderId;
import com.damon.object_trace.Aggregate;

public interface IOrderGateway {

    Aggregate<Order> get(OrderId orderId);

    /**
     * @param orderAggregate
     * @throws OptimisticLockException
     */
    void save(Aggregate<Order> orderAggregate);

}
