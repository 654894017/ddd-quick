package com.damon.demo.domain.order.entity;

import com.damon.demo.common.domain.AggregateRoot;
import lombok.Data;

@Data
public class OrderLog implements AggregateRoot<OrderLog> {

    private Long id;

    private Long orderId;

    private Integer status;

    private int version;

    public OrderLog(Long id, Long orderId, Integer status) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.version = 0;
    }

    public OrderLog() {
    }

    public void create() {
        this.status = OrderLogStatus.CREATE;
    }

    public void rollback() {
        this.status = OrderLogStatus.ROLLBACK;
    }

    public void commit() {
        this.status = OrderLogStatus.COMMIT;
    }

    public void finish() {
        this.status = OrderLogStatus.FINISHED;
    }


    public boolean isFinished() {
        return this.status == OrderLogStatus.FINISHED;
    }

    public boolean isRollbacked() {
        return false;
    }

    public boolean isSubmitted() {
        return false;
    }

    public boolean isCreated() {
        return false;
    }
}
