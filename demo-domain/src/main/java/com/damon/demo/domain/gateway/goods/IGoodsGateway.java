package com.damon.demo.domain.gateway.goods;

import java.util.List;
import java.util.Set;

public interface IGoodsGateway {

    List<GoodsBasicInfoDTO> queryGoodsBasicInfo(Set<Long> goodsIds);

}
