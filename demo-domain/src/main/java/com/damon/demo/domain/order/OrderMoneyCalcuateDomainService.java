package com.damon.demo.domain.order;

import com.damon.demo.domain.exception.BusinessException;
import com.damon.demo.domain.gateway.coupon.CouponDTO;
import com.damon.demo.domain.gateway.coupon.ICouponGateway;
import com.damon.demo.domain.gateway.point.IPointGateway;
import com.damon.demo.domain.order.entity.Order;

//@Service
//@RequiredArgsConstructor
public class OrderMoneyCalcuateDomainService {

    private IPointGateway pointGateway;
    private ICouponGateway couponGateway;

    public Long calculatePayMoney(Order order) {
        Long pointsDeductionMoney = pointGateway.calculateDeductionMoney(order.getDeductionPoints(), order.getOrderSubmitUserId());
        CouponDTO coupon = couponGateway.get(order.getCouponId(), order.getOrderSubmitUserId());
        if (coupon == null) {
            throw new BusinessException("找不到对应的优惠券信息 : " + order.getCouponId());
        }
        Long orderOriginalMoney = order.calcuateOrderOriginalMoney();
        Long actualPayMoney = orderOriginalMoney - coupon.getDeductionMoney() - pointsDeductionMoney;
        Long money = actualPayMoney < 0 ? 0 : actualPayMoney;
        return money;
    }


}
