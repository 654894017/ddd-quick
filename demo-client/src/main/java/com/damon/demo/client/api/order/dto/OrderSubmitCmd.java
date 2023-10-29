package com.damon.demo.client.api.order.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderSubmitCmd {

    private Long couponId;

    private Long deductionPoints;
    @NotNull
    private Long consigneeId;
    @NotNull
    @NotEmpty
    private List<Goods> goods;
    @NotNull
    private Long customerId;
    @NotNull
    private Long orderSubmitUserId;

    @Data
    public static class Goods {
        @NotNull
        private Long goodsId;
        @NotNull
        @Min(1)
        private Integer amount;
    }


}
