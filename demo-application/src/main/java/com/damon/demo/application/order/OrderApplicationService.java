package com.damon.demo.application.order;

import com.damon.demo.application.order.command.OrderSubmitCmdExe;
import com.damon.demo.client.api.order.IOrderApplicationService;
import com.damon.demo.client.api.order.dto.OrderSubmitCmd;
import com.damon.demo.client.api.order.dto.OrderSubmitRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 订单管理应用服务
 */
@Service
@RequiredArgsConstructor
public class OrderApplicationService implements IOrderApplicationService {

    private final OrderSubmitCmdExe orderSubmitCmdExe;
    @Override
    public OrderSubmitRespDTO submitOrder(OrderSubmitCmd cmd) {
        return orderSubmitCmdExe.execute(cmd);
    }

    @Override
    public void checkOrderSubmitStatus() {
        orderSubmitCmdExe.checkTrasactionStatus();
    }


}
