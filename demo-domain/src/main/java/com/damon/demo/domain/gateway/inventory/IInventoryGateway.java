package com.damon.demo.domain.gateway.inventory;

public interface IInventoryGateway {
    /**
     * @param cmd
     * @return
     * @throws InventoryDeficiencyException
     */
    void tryDeduction(InventoryDedcutionCmd cmd);

    void commitDeduction(Long bizId);

    void cancelDeduction(Long bizId);


}
