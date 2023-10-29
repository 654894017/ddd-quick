package com.damon.demo.domain.gateway.customer;

import lombok.Data;

@Data
public class ConsigneeInfoDTO {
    private Long consigneeId;
    private String name;
    private String shippingAddress;
    private String mobile;

}
