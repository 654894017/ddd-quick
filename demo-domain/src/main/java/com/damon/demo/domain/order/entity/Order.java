package com.damon.demo.domain.order.entity;

import com.damon.demo.domain.base_type.domain.AggregateRoot;
import com.damon.demo.domain.exception.BusinessException;
import com.damon.demo.domain.order.OrderMoneyCalcuateDomainService;
import com.damon.tcc.BizId;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@ToString
public class Order implements AggregateRoot<Order>, BizId {

    private Long id;
    private Integer status;
    private Long createTime;
    private Long updateTime;
    private Consignee consignee;
    private List<OrderItem> orderItems;
    private Long totalMoney;
    private Long actualPayMoney;
    private int version;
    private Long couponId;
    private Long deductionPoints;
    private Long orderSubmitUserId;
    private int delete;
    private Long sellerId;

    public boolean canDedcutionPoints() {
        return deductionPoints > 0;
    }

    public boolean canDedcutionCoupon() {
        return couponId > 0;
    }

    public void changeShippingAddress(String address) {
        if (status != 1) {
            throw new RuntimeException("未开始拣货的订单才可以更新收货地址");
        }
        consignee.changeShippingAddress(address);
    }

    public void cancel() {
        if (status != 2) {
            throw new RuntimeException("未开始拣货的订单才可以更新收货地址");
        }
        this.status = 5;
    }


    public void submit(OrderMoneyCalcuateDomainService orderMoneyCalcuateDomainService) {
        if (status != OrderStatus.CREATE) {
            throw new RuntimeException("订单已提交，不允许重复提交");
        }
        this.actualPayMoney = orderMoneyCalcuateDomainService.calculatePayMoney(this);
        this.totalMoney = calcuateOrderOriginalMoney();
        this.status = OrderStatus.WATING_PAY;
    }

    public Long calcuateOrderOriginalMoney() {
        Long money = orderItems.stream().mapToLong(item -> item.getPrice() * item.getAmount()).sum();
        return money;
    }

    public Set<Long> getGoodsIds() {
        return orderItems.stream().map(OrderItem::getGoodsId).collect(Collectors.toSet());
    }

    public void delete() {
        this.delete = 1;
    }

    public boolean isSubmitted() {
        return false;
    }

    public String getOrderGoodsName(Long goodsId) {
        return orderItems.stream().filter(orderItem ->
                orderItem.getGoodsId().equals(goodsId)
        ).findFirst().map(OrderItem::getGoodsName).orElseThrow(
                () -> new BusinessException("无效的商品id")
        );
    }

    @Override
    public Long getBizId() {
        return id;
    }
}
