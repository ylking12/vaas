/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
 *  com.etas.vaas.admin.config.MybatisPlusFillConfig
 *  lombok.Generated
 *  org.apache.ibatis.reflection.MetaObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class MybatisPlusFillConfig
implements MetaObjectHandler {
    private static final Logger log = LoggerFactory.getLogger(MybatisPlusFillConfig.class);

    public void insertFill(MetaObject metaObject) {
        log.debug("Start Insert Auto-Fill...");
        this.setFieldValByName("updateAt", System.currentTimeMillis(), metaObject);
    }

    public void updateFill(MetaObject metaObject) {
        log.debug("Start Update Auto-Fill...");
        this.setFieldValByName("updateAt", System.currentTimeMillis(), metaObject);
    }
}

