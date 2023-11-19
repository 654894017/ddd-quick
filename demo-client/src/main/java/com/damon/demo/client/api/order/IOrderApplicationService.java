package com.damon.demo.client.api.order;

import com.damon.demo.client.api.order.dto.OrderSubmitCmd;
import com.damon.demo.client.api.order.dto.OrderSubmitRespDTO;

public interface IOrderApplicationService {
    OrderSubmitRespDTO submitOrder(OrderSubmitCmd cmd);

    void checkFailedOrderLogtStatus();

    void checkDeadOrderLogStatus();
}
