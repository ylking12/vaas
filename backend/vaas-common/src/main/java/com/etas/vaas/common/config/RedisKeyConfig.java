/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.common.config;

import com.etas.vaas.common.entity.RedisKey;
import com.etas.vaas.common.mapper.RedisKeyMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RedisKeyConfig {
    private RedisKey instance;
    @Resource
    private RedisKeyMapper redisKeyMapper;

    @PostConstruct
    void init() {
        RedisKey key = (RedisKey)this.redisKeyMapper.selectOne(null);
        if (key == null) {
            throw new IllegalStateException("RedisKey \u8868\u672a\u521d\u59cb\u5316\uff0c\u8bf7\u5148\u63d2\u5165\u9ed8\u8ba4\u914d\u7f6e");
        }
        this.instance = key;
    }

    public RedisKey getInstance() {
        return this.instance;
    }
}

