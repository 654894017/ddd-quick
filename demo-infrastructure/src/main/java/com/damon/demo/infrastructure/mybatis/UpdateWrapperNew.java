package com.damon.demo.infrastructure.mybatis;


import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.util.Set;

public class UpdateWrapperNew<T> extends UpdateWrapper<T> {

    public void set(Set<String> changedFields, T t) {
        for (String field : changedFields) {
            set(field, ReflectUtil.getFieldValue(t, field));
        }
    }

}
