package com.damon.demo.domain.gateway.coupon;

import com.damon.demo.common.exception.TechnicalException;

public class CouponInvalidException extends TechnicalException {
    private Long couponId;

    public CouponInvalidException(String message, Long couponId) {
        super(message, null, false, false);
        this.couponId = couponId;
    }
}
