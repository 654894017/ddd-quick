package com.damon.demo.domain.gateway.coupon;

public interface ICouponGateway {
    /**
     * @param orderId
     * @param couponIds
     * @param orderSubmitUserId
     * @throws CouponInvalidException
     */
    CouponDTO tryDeductionCoupon(Long orderId, Long couponIds, Long orderSubmitUserId);

    void commitDeductionCoupon(Long orderId);

    void cancelDeductionCoupon(Long orderId);

    CouponDTO get(Long couponId, Long orderSubmitUserId);
}
