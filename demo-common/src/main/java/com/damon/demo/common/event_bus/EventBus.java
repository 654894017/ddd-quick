package com.damon.demo.common.event_bus;

public class EventBus {
    private static com.google.common.eventbus.EventBus eventBus;

    static {
        eventBus = new com.google.common.eventbus.EventBus();
    }

    public static void post(Object object) {
        eventBus.post(object);
    }

    public static void register(Object object) {
        eventBus.register(object);
    }

}
