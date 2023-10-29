package com.damon.demo.domain.gateway.customer;

public interface ICustomerGateway {
    ConsigneeInfoDTO getConsigneeInfo(Long customerId, Long consigneeId);

}
