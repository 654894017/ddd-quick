package com.damon.demo.application.order.command;

import com.damon.object_trace.Aggregate;
import com.damon.demo.domain.gateway.coupon.ICouponGateway;
import com.damon.demo.domain.gateway.inventory.IInventoryGateway;
import com.damon.demo.domain.gateway.point.IPointGateway;
import com.damon.demo.domain.gateway.shopping_cart.IShoppingCartGateway;
import com.damon.demo.domain.order.IOrderGateway;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderId;
import com.damon.demo.domain.order.entity.OrderLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderSubmitStatusCheckCmdExe {
    private final IInventoryGateway inventoryGateway;
    private final IPointGateway pointGateway;
    private final ICouponGateway couponGateway;
    private final OrderLogDomainService orderLogDomainService;
    private final IShoppingCartGateway shoppingCartGateway;
    private final IOrderGateway orderGateway;

    public void execute(OrderSubmitStatusCheckCmd cmd) {

        OrderLog orderLog = orderLogDomainService.get(new OrderId(cmd.getOrderId()));
        if (orderLog == null) {
            log.warn("找不到对应的订单日志信息, 订单id :{}", cmd.getOrderId());
            return;
        }

        if (orderLog.isFinished() || orderLog.isRollbacked()) {
            log.warn("订单已回滚或已提交，不执行订单提交状态检查, 订单id :{}", cmd.getOrderId());
            return;
        }

        if (orderLog.isSubmitted()) {
            Aggregate<Order> orderAggregate = orderGateway.get(new OrderId(orderLog.getOrderId()));
            Order order = orderAggregate.getRoot();
            if (order == null) {
                log.error("找不到对应的订单信息, 订单id :{}", cmd.getOrderId());
                return;
            }
            shoppingCartGateway.remove(order.getOrderSubmitUserId(), order.getGoodsIds());
            inventoryGateway.commitDeduction(cmd.getOrderId());
            pointGateway.commitDeductionPoints(cmd.getOrderId());
            couponGateway.commitDeductionCoupon(cmd.getOrderId());
            orderLogDomainService.finishLog(orderLog);
            log.info("执行完成订单补提交操作，订单id :{}", cmd.getOrderId());
        } else if (orderLog.isCreated()) {
            inventoryGateway.cancelDeduction(cmd.getOrderId());
            pointGateway.cancelDeductionPoints(cmd.getOrderId());
            couponGateway.cancelDeductionCoupon(cmd.getOrderId());
            orderLogDomainService.rollbackLog(orderLog);
            log.info("执行完成订单取消操作，订单id :{}", cmd.getOrderId());
        } else {
            log.error("订单id:{}, 无效的日志状态 : {}", orderLog.getOrderId(), orderLog.getStatus());
        }
    }

}
