package com.damon.demo.application.order.command;

import cn.hutool.core.thread.NamedThreadFactory;
import com.damon.demo.application.order.OrderAssembler;
import com.damon.demo.client.api.order.dto.OrderSubmitCmd;
import com.damon.demo.client.api.order.dto.OrderSubmitRespDTO;
import com.damon.demo.domain.gateway.coupon.CouponInvalidException;
import com.damon.demo.domain.gateway.coupon.ICouponGateway;
import com.damon.demo.domain.gateway.inventory.GoodsSoldoutException;
import com.damon.demo.domain.gateway.inventory.IInventoryGateway;
import com.damon.demo.domain.gateway.inventory.InventoryDedcutionCmd;
import com.damon.demo.domain.gateway.inventory.InventoryDeficiencyException;
import com.damon.demo.domain.gateway.point.IPointGateway;
import com.damon.demo.domain.gateway.point.PointsDeficiencyException;
import com.damon.demo.domain.gateway.shopping_cart.IShoppingCartGateway;
import com.damon.demo.domain.order.IOrderGateway;
import com.damon.demo.domain.order.OrderMoneyCalcuateDomainService;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderId;
import com.damon.object_trace.AggregateFactory;
import com.damon.tcc.TccConfig;
import com.damon.tcc.TccTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderSubmitCmdExe extends TccTemplateService<Long, Order> {
    private final OrderAssembler orderAssembler;
    private final IInventoryGateway inventoryGateway;
    private final IPointGateway pointGateway;
    private final ICouponGateway couponGateway;
    private final IShoppingCartGateway shoppingCartGateway;
    private final OrderMoneyCalcuateDomainService orderMoneyCalcuateDomainService;
    private final IOrderGateway orderGateway;
    @Autowired
    public OrderSubmitCmdExe(TccConfig tccConfig,
                             OrderAssembler orderAssembler,
                             IInventoryGateway inventoryGateway,
                             IPointGateway pointGateway,
                             ICouponGateway couponGateway,
                             IShoppingCartGateway shoppingCartGateway,
                             OrderMoneyCalcuateDomainService orderMoneyCalcuateDomainService,
                             IOrderGateway orderGateway) {
        super(tccConfig);
        this.orderAssembler = orderAssembler;
        this.inventoryGateway = inventoryGateway;
        this.pointGateway = pointGateway;
        this.couponGateway = couponGateway;
        this.shoppingCartGateway = shoppingCartGateway;
        this.orderMoneyCalcuateDomainService = orderMoneyCalcuateDomainService;
        this.orderGateway = orderGateway;
    }

    public void executeFailedCheck() {
        super.executeFailedLogCheck();
    }

    public void executeDeadCheck() {
        super.executeDeadLogCheck();
    }

    public OrderSubmitRespDTO execute(OrderSubmitCmd cmd) {
        try {
            Order order = orderAssembler.assembler(cmd);
            Long orderId = super.process(order);
            return new OrderSubmitRespDTO(1, orderId);
        } catch (PointsDeficiencyException e) {
            return new OrderSubmitRespDTO(-1);
        } catch (CouponInvalidException e) {
            return new OrderSubmitRespDTO(-1);
        } catch (InventoryDeficiencyException e) {
            return new OrderSubmitRespDTO(-1);
        } catch (GoodsSoldoutException e) {
            Set<Long> soldoutGoodsIds = e.getSoldoutGoodsIds();
            return new OrderSubmitRespDTO(-1);
        } catch (Exception e) {
            log.error("系统异常,提交订单失败, 订单信息: {}", cmd, e);
            return new OrderSubmitRespDTO(-1);
        }
    }

    @Override
    protected Order callbackParameter(Long bizId) {
        return orderGateway.get(new OrderId(bizId)).getRoot();
    }

    @Override
    protected void tryPhase(Order order) {
        Set<InventoryDedcutionCmd.Item> itemSet = order.getOrderItems().stream().map(item ->
                new InventoryDedcutionCmd.Item(item.getGoodsId(), item.getAmount())
        ).collect(Collectors.toSet());
        inventoryGateway.tryDeduction(new InventoryDedcutionCmd(order.getId(), itemSet));
        if (order.canDedcutionPoints()) {
            pointGateway.tryDeductionPoints(order.getId(), order.getDeductionPoints(), order.getOrderSubmitUserId());
        }
        if (order.canDedcutionCoupon()) {
            couponGateway.tryDeductionCoupon(order.getId(), order.getCouponId(), order.getOrderSubmitUserId());
        }
        order.submit(orderMoneyCalcuateDomainService);
    }

    @Override
    protected Long executeLocalTransactionPhase(Order order) {
        orderGateway.create(AggregateFactory.createAggregate(order));
        return order.getId();
    }

    @Override
    protected void commitPhase(Order order) {
        shoppingCartGateway.remove(order.getOrderSubmitUserId(), order.getGoodsIds());
        inventoryGateway.commitDeduction(order.getId());
        pointGateway.commitDeductionPoints(order.getId());
        couponGateway.commitDeductionCoupon(order.getId());

    }

    @Override
    protected void cancelPhase(Order order) {
        inventoryGateway.cancelDeduction(order.getId());
        if (order.canDedcutionPoints()) {
            pointGateway.cancelDeductionPoints(order.getId());
        }
        if (order.canDedcutionCoupon()) {
            couponGateway.cancelDeductionCoupon(order.getId());
        }
    }
}
