/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.component.FleetManagementComponent
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.dto.KtPackageFrame
 *  com.etas.vaas.common.dto.KtPackageFrame$RequestData
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  com.etas.vaas.common.utils.TimeUtils
 *  com.etas.vaas.receiver.service.KtService
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.receiver.service;

import com.etas.vaas.common.component.FleetManagementComponent;
import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dto.KtPackageFrame;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import com.etas.vaas.common.utils.TimeUtils;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KtService {
    private static final Logger log = LoggerFactory.getLogger(KtService.class);
    @Value(value="${redis.kt-max-queue-size}")
    private Integer maxQueueSize;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Resource
    private FleetManagementComponent fleetManagementComponent;

    public boolean handleKT710Post(KtPackageFrame kt710) {
        log.debug("handleKT710Post:{}", (Object)JsonUtils.toStr((Object)kt710));
        String sn = ((KtPackageFrame.RequestData)kt710.getData().get(0)).getSn();
        if (!this.fleetManagementComponent.isKtCar(sn)) {
            return false;
        }
        LocalDateTime readableCrtTime = LocalDateTime.now();
        kt710.setReceivedAt(TimeUtils.objToStr((LocalDateTime)readableCrtTime));
        if (this.fleetManagementComponent.snIllegal(sn)) {
            log.warn("Invalid device with sn: {} trying to upload", (Object)sn);
            return false;
        }
        int groupId = this.fleetManagementComponent.getKtGroupId(sn);
        String key = this.redisKeyConfig.getInstance().getKtQueue() + groupId;
        if (this.redisUtils.listSize(key) < (long)this.maxQueueSize.intValue()) {
            this.redisUtils.rightPush(key, (Object)kt710);
        } else {
            log.warn("{} list contains too many items (>{})", (Object)key, (Object)this.maxQueueSize);
            this.redisUtils.leftPopWithCount(key, (long)(this.maxQueueSize / 2));
            log.info("reduce redis list: {}'s size down, pop {} out...", (Object)key, (Object)(this.maxQueueSize / 2));
        }
        log.debug("publish to topic: {}, message body: {}", (Object)this.redisKeyConfig.getInstance().getKtTopic(), (Object)groupId);
        this.redisUtils.publishMessage(this.redisKeyConfig.getInstance().getKtTopic(), String.valueOf(groupId));
        return true;
    }

    public KtService() {
    }
}

