package com.damon.demo.common;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;

public class TransmittableContext {
    private final static TransmittableThreadLocal<Map<String, Object>> threadLocal = new TransmittableThreadLocal<>();

    public static void put(String key, Object value) {
        if (threadLocal.get() == null) {
            threadLocal.set(new HashMap<>(4));
        }
        threadLocal.get().put(key, value);
    }

    public static <V> V get(String key) {
        Map<String, Object> map = threadLocal.get();
        return map == null ? null : (V) threadLocal.get().get(key);
    }

    public static void remove(String key) {
        Map<String, Object> map = threadLocal.get();
        if (map != null) {
            threadLocal.get().remove(key);
        }
    }

    public static void clear() {
        threadLocal.remove();
    }

}
