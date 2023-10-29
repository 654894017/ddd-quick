package com.damon.demo.domain.gateway.point;

import com.damon.demo.common.exception.BusinessException;

public class PointsDeficiencyException extends BusinessException {
    public PointsDeficiencyException(String message) {
        super(message);
    }
}
