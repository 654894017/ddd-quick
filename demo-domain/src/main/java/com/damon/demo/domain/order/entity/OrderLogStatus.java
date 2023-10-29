package com.damon.demo.domain.order.entity;

public interface OrderLogStatus {

    int CREATE = 1;
    int ROLLBACK = 2;
    int COMMIT = 4;

    int FINISHED = 5;


}
