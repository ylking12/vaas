/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.dto.CoordinateFrame
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.MathUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  com.etas.vaas.common.utils.TimeUtils
 *  com.etas.vaas.receiver.dto.CachedVehiclePosition
 *  com.etas.vaas.receiver.service.VehicleService
 *  jakarta.annotation.Resource
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.receiver.service;

import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dto.CoordinateFrame;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.MathUtils;
import com.etas.vaas.common.utils.RedisUtils;
import com.etas.vaas.common.utils.TimeUtils;
import com.etas.vaas.receiver.dto.CachedVehiclePosition;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Value(value="${gps.clean-interval}")
    private Long gpsDataCleanInterval;

    public void appendCoordinateData(CoordinateFrame coordinateFrame) {
        double lon = coordinateFrame.getLongitude();
        double lat = coordinateFrame.getLatitude();
        String deviceId = coordinateFrame.getDeviceId();
        long timestamp = coordinateFrame.getTimestamp();
        int speed = this.calculateSpeed(coordinateFrame);
        CachedVehiclePosition cacheLocation = new CachedVehiclePosition(Double.valueOf(lon), Double.valueOf(lat), Integer.valueOf(speed), Long.valueOf(timestamp), TimeUtils.convertTsToDatetime((long)timestamp));
        log.debug("cacheLocation: {}", (Object)cacheLocation);
        String cacheLocationString = JsonUtils.toStr((Object)cacheLocation);
        String key = this.redisKeyConfig.getInstance().getVehicleInfoPrefix() + deviceId;
        this.redisUtils.addToZSet(key, cacheLocationString, (double)timestamp);
        long currentTime = System.currentTimeMillis();
        this.redisUtils.removeFromZSetByScore(key, 0.0, (double)currentTime - (double)this.gpsDataCleanInterval.longValue());
    }

    private int calculateSpeed(CoordinateFrame currentFrame) {
        String deviceId = currentFrame.getDeviceId();
        try {
            String previousInfoString = this.redisUtils.getMaxScoreMember(this.redisKeyConfig.getInstance().getVehicleInfoPrefix() + deviceId);
            log.debug("previousInfoString: {}", (Object)previousInfoString);
            if (StringUtils.isBlank((CharSequence)previousInfoString)) {
                log.warn("no previous info in redis for device-{}", (Object)deviceId);
                return 0;
            }
            CachedVehiclePosition previousInfo = (CachedVehiclePosition)JsonUtils.toObj((String)previousInfoString, CachedVehiclePosition.class);
            log.debug("previousInfo: {}", (Object)previousInfo);
            if (ObjectUtils.isEmpty((Object)previousInfo)) {
                log.warn("previousInfo for device-{} is null!", (Object)deviceId);
                return 0;
            }
            if (ObjectUtils.isEmpty((Object)previousInfo.getSpeed())) {
                log.warn("previous speed for device-{} is null!", (Object)deviceId);
                return 0;
            }
            if (previousInfo.getTimestamp() != null && currentFrame.getTimestamp() - previousInfo.getTimestamp() > 1000L) {
                double meter = MathUtils.haversineDistance((double)previousInfo.getLongitude(), (double)previousInfo.getLatitude(), (double)currentFrame.getLongitude(), (double)currentFrame.getLatitude());
                if (meter >= 500.0) {
                    log.error("calSpd gps has wrong! meter={}", (Object)meter);
                    log.error("calSpd previous gps lat={},lng={}", (Object)previousInfo.getLatitude(), (Object)previousInfo.getLongitude());
                    log.error("calSpd current gps lat={},lng={}", (Object)currentFrame.getLatitude(), (Object)currentFrame.getLongitude());
                    return previousInfo.getSpeed();
                }
                double deltaTime = (double)(currentFrame.getTimestamp() - previousInfo.getTimestamp()) / 1000.0;
                return (int)(meter / deltaTime * 3.6);
            }
        }
        catch (Exception e) {
            log.error("calSpd exception", (Throwable)e);
        }
        return 0;
    }
}

