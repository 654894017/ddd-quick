package com.damon.demo.domain.gateway.shopping_cart;

import java.util.Set;

public interface IShoppingCartGateway {
    boolean remove(Long userId, Set<Long> goodsIds);
}
