package com.damon.demo.domain.gateway.point;

public interface IPointGateway {
    /**
     * @param bizId
     * @param points
     * @throws PointsDeficiencyException
     */
    void tryDeductionPoints(Long bizId, Long points, Long orderSubmitUserId);

    void commitDeductionPoints(Long bizId);

    void cancelDeductionPoints(Long bizId);

    Long calculateDeductionMoney(Long deductionPoints, Long orderSubmitUserId);
}
