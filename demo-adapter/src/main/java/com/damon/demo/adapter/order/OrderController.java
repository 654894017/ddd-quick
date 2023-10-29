package com.damon.demo.adapter.order;

import com.damon.demo.client.api.order.IOrderApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo/v0.1/order")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderApplicationService orderApplicationService;

}
