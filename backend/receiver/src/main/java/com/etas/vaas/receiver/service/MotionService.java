/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.component.FleetManagementComponent
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.dto.MotionFrame
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  com.etas.vaas.receiver.service.MotionService
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.receiver.service;

import com.etas.vaas.common.component.FleetManagementComponent;
import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dto.MotionFrame;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MotionService {
    private static final Logger log = LoggerFactory.getLogger(MotionService.class);
    @Value(value="${redis.motion-max-queue-size}")
    private Integer maxQueueSize;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Resource
    private FleetManagementComponent fleetManagementComponent;

    public void handleMotionData(MotionFrame frame) {
        log.debug("handleData:{}", (Object)JsonUtils.toStr((Object)frame));
        String deviceId = frame.getDeviceId();
        if (!this.fleetManagementComponent.isMotionCar(deviceId)) {
            log.debug("not motion car, deviceId: {}", (Object)deviceId);
            return;
        }
        if (this.fleetManagementComponent.deviceIdIllegal(deviceId)) {
            log.warn("Invalid device with deviceId: {} trying to upload", (Object)deviceId);
            return;
        }
        frame.setReceivedTimestamp(Long.valueOf(System.currentTimeMillis()));
        int groupId = this.fleetManagementComponent.getMotionGroupId(deviceId);
        String key = this.redisKeyConfig.getInstance().getMotionQueue() + groupId;
        if (this.redisUtils.listSize(key) < (long)this.maxQueueSize.intValue()) {
            this.redisUtils.rightPush(key, (Object)frame);
        } else {
            log.warn("{} list contains too many items (>{})", (Object)key, (Object)this.maxQueueSize);
            this.redisUtils.leftPopWithCount(key, (long)(this.maxQueueSize / 2));
            log.info("reduce redis list: {}'s size down, pop {} out...", (Object)key, (Object)(this.maxQueueSize / 2));
        }
        log.debug("publish to topic: {}, message body: {}", (Object)this.redisKeyConfig.getInstance().getMotionTopic(), (Object)groupId);
        this.redisUtils.publishMessage(this.redisKeyConfig.getInstance().getMotionTopic(), String.valueOf(groupId));
    }
}

