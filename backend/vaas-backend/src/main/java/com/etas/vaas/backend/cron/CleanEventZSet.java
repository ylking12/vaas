/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.cron.CleanEventZSet
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.utils.RedisUtils
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.backend.cron;

import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanEventZSet {
    private static final Logger log = LoggerFactory.getLogger(CleanEventZSet.class);
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisKeyConfig redisKeyConfig;

    @Scheduled(fixedRate=3600000L)
    private void clean() {
        log.info("cleaning outdate event from zset...");
        long current = System.currentTimeMillis();
        long oneDayAgo = current - 86400000L;
        this.redisUtils.removeFromZSetByScore(this.redisKeyConfig.getInstance().getBumpEventKey(), -9.223372036854776E18, (double)oneDayAgo);
        this.redisUtils.removeFromZSetByScore(this.redisKeyConfig.getInstance().getSlipEventKey(), -9.223372036854776E18, (double)oneDayAgo);
        this.redisUtils.removeFromZSetByScore(this.redisKeyConfig.getInstance().getPondingEventKey(), -9.223372036854776E18, (double)oneDayAgo);
    }
}

