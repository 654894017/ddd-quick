package com.damon.demo.application.order.command;

import cn.hutool.core.thread.NamedThreadFactory;
import com.damon.demo.application.order.OrderAssembler;
import com.damon.demo.client.api.order.dto.OrderSubmitCmd;
import com.damon.demo.client.api.order.dto.OrderSubmitRespDTO;
import com.damon.demo.domain.gateway.coupon.ICouponGateway;
import com.damon.demo.domain.gateway.inventory.IInventoryGateway;
import com.damon.demo.domain.gateway.inventory.InventoryDedcutionCmd;
import com.damon.demo.domain.gateway.inventory.InventoryTryDeductionResultDTO;
import com.damon.demo.domain.gateway.point.IPointGateway;
import com.damon.demo.domain.gateway.shopping_cart.IShoppingCartGateway;
import com.damon.demo.domain.order.OrderMoneyCalcuateDomainService;
import com.damon.demo.domain.order.entity.Order;
import com.damon.demo.domain.order.entity.OrderLog;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderSubmitTest {
    private final OrderCreateDomainService orderCreateDomainService;
    private final OrderLogDomainService orderLogDomainService;
    private final OrderMoneyCalcuateDomainService orderMoneyCalcuateDomainService;
    private final OrderAssembler orderAssembler;
    private final IInventoryGateway inventoryGateway;
    private final IPointGateway pointGateway;
    private final ICouponGateway couponGateway;
    private final IShoppingCartGateway shoppingCartGateway;
    private final ExecutorService executorService = new ThreadPoolExecutor(50, 256,
            10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(512),
            new NamedThreadFactory("order-aync-commt-pool-", false)
    );

    public OrderSubmitRespDTO execute(OrderSubmitCmd cmd) {
        Order order = orderAssembler.assembler(cmd);
        OrderLog orderLog = orderLogDomainService.createLog(order.getId());
        try {
            InventoryTryDeductionResultDTO result = tryDedcutionInventory(order);
            if (result.isInventoryScarceGoods()) {
                return OrderSubmitRespDTO.builder().orderSubmitStatus(-1).inventoryScarceGoods(
                        ImmutableMap.of(result.getInventoryScarceGoodsId(), order.getOrderGoodsName(result.getInventoryScarceGoodsId()))
                ).build();
            }
            if (result.isSoldoutGoods()) {
                return OrderSubmitRespDTO.builder().orderSubmitStatus(-2).soldoutGoods(
                        ImmutableMap.of(result.getInventoryScarceGoodsId(), order.getOrderGoodsName(result.getInventoryScarceGoodsId()))
                ).build();
            }
            if (order.canDedcutionPoints()) {
                pointGateway.tryDeductionPoints(order.getId(), order.getDeductionPoints(), order.getOrderSubmitUserId());
            }
            if (order.canDedcutionCoupon()) {
                couponGateway.tryDeductionCoupon(order.getId(), order.getCouponId(), order.getOrderSubmitUserId());
            }
        } catch (Exception e) {
            log.error("订单创建失败, 订单信息: {} ", order, e);
            try {
                inventoryGateway.cancelDeduction(order.getId());
                if (order.canDedcutionPoints()) {
                    pointGateway.cancelDeductionPoints(order.getId());
                }
                if (order.canDedcutionCoupon()) {
                    couponGateway.cancelDeductionCoupon(order.getId());
                }
                orderLogDomainService.rollbackLog(orderLog);
                log.info("回滚订单信息成功: {} ", order);
            } catch (Exception exception) {
                log.error("回滚订单失败: {} ", order);
            }
            return new OrderSubmitRespDTO(-1);
        }

        try {
            order.submit(orderMoneyCalcuateDomainService);
            orderCreateDomainService.create(order, orderLog);
        } catch (Exception e) {
            log.error("提交订单信息失败，等待系统校正，订单信息: {} ", order);
            return new OrderSubmitRespDTO(-1);
        }

        log.info("订单创建成功, 订单信息: {} ", order);
        executorService.submit(() -> {
            commitOrder(order, orderLog);
        });
        return new OrderSubmitRespDTO(1, order.getId());
    }

    private void commitOrder(Order order, OrderLog orderLog) {
        try {
            shoppingCartGateway.remove(order.getOrderSubmitUserId(), order.getGoodsIds());
            inventoryGateway.commitDeduction(order.getId());
            pointGateway.commitDeductionPoints(order.getId());
            couponGateway.commitDeductionCoupon(order.getId());
            orderLogDomainService.finishLog(orderLog);
            log.info("异步Commit订单成功, 订单信息: {} ", order);
        } catch (Exception e) {
            log.error("异步Commit订单信息失败，等待系统校正，订单信息: {} ", order);
        }
    }

    private InventoryTryDeductionResultDTO tryDedcutionInventory(Order order) {

        Set<InventoryDedcutionCmd.Item> itemSet = order.getOrderItems().stream().map(item ->
                new InventoryDedcutionCmd.Item(item.getGoodsId(), item.getAmount())
        ).collect(Collectors.toSet());

        inventoryGateway.tryDeduction(new InventoryDedcutionCmd(order.getId(), itemSet));
        return null;
    }


}
