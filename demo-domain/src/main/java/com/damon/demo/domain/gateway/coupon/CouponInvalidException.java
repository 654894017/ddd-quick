package com.damon.demo.domain.gateway.coupon;

import com.damon.demo.domain.exception.BusinessException;

public class CouponInvalidException extends BusinessException {
    private Long couponId;

    public CouponInvalidException(String message, Long couponId) {
        super(message, null, false, false);
        this.couponId = couponId;
    }
}
