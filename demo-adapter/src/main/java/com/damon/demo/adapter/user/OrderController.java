package com.damon.demo.adapter.user;

import com.damon.demo.client.api.order.IOrderApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("demo/v0.1/order")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderApplicationService orderApplicationService;

}