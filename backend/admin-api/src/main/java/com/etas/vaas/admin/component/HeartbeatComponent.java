/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.component.HeartbeatComponent
 *  com.etas.vaas.admin.dto.GetHeartbeatResp
 *  com.etas.vaas.admin.dto.GetHeartbeatResp$EachHeartbeatInfo
 *  com.etas.vaas.common.utils.RedisUtils
 *  com.etas.vaas.common.utils.TimeUtils
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.admin.component;

import com.etas.vaas.admin.dto.GetHeartbeatResp;
import com.etas.vaas.common.utils.RedisUtils;
import com.etas.vaas.common.utils.TimeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class HeartbeatComponent {
    private static final Logger log = LoggerFactory.getLogger(HeartbeatComponent.class);
    @Resource
    private RedisUtils redisUtils;
    private static final int DEVICE_CAPACITY = 175;
    private volatile int maxKtOnline = 0;
    private volatile int maxMotionOnline = 0;
    private volatile int maxLocationOnline = 0;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private volatile GetHeartbeatResp resp = new GetHeartbeatResp();

    @PostConstruct
    void init() {
        this.maxKtOnline = NumberUtils.toInt((String)this.redisUtils.getValue("vaas:stat:max-kt-on"), (int)0);
        this.maxMotionOnline = NumberUtils.toInt((String)this.redisUtils.getValue("vaas:stat:max-motion-on"), (int)0);
        this.maxLocationOnline = NumberUtils.toInt((String)this.redisUtils.getValue("vaas:stat:max-location-on"), (int)0);
        this.scheduler.scheduleAtFixedRate(() -> this.refreshHeartbeat(), 0L, 5L, TimeUnit.SECONDS);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void refreshHeartbeat() {
        try {
            long now = System.currentTimeMillis();
            Map<String, String> locationHeartbeatMap = this.redisUtils.getEntireHash("vaas:heartbeat:location");
            Map<String, String> motionHeartbeatMap = this.redisUtils.getEntireHash("vaas:heartbeat:motion");
            Map<String, String> ktHeartbeatMap = this.redisUtils.getEntireHash("vaas:heartbeat:kt");
            HashSet<String> allDeviceIds = new HashSet<String>(175);
            allDeviceIds.addAll(locationHeartbeatMap.keySet());
            allDeviceIds.addAll(motionHeartbeatMap.keySet());
            allDeviceIds.addAll(ktHeartbeatMap.keySet());
            ArrayList<GetHeartbeatResp.EachHeartbeatInfo> heartbeatInfoList = new ArrayList<GetHeartbeatResp.EachHeartbeatInfo>(175);
            int tempKtOnlineCount = 0;
            int tempMotionOnlineCount = 0;
            int tempLocationOnlineCount = 0;
            for (String deviceId : allDeviceIds) {
                String locationValue;
                String motionValue;
                GetHeartbeatResp.EachHeartbeatInfo heartbeatInfo = new GetHeartbeatResp.EachHeartbeatInfo();
                heartbeatInfo.setDeviceId(deviceId);
                String ktValue = (String)ktHeartbeatMap.get(deviceId);
                if (ktValue != null) {
                    long ts = Long.parseLong(ktValue);
                    heartbeatInfo.setKtLastOnlineTimestamp(Long.valueOf(ts));
                    heartbeatInfo.setKtLastOnlineTimestamp(Long.valueOf(ts));
                    heartbeatInfo.setKtLastOnlineTime(TimeUtils.objToStr(TimeUtils.timestampToLocalDateTime((long)ts)));
                    if (now - ts < 30000L) {
                        heartbeatInfo.setKtOnline(true);
                        ++tempKtOnlineCount;
                    }
                }
                if ((motionValue = (String)motionHeartbeatMap.get(deviceId)) != null) {
                    long ts = Long.parseLong(motionValue);
                    heartbeatInfo.setMotionLastOnlineTimestamp(Long.valueOf(ts));
                    heartbeatInfo.setMotionLastOnlineTime(TimeUtils.objToStr(TimeUtils.timestampToLocalDateTime((long)ts)));
                    if (now - ts < 30000L) {
                        heartbeatInfo.setMotionOnline(true);
                        ++tempMotionOnlineCount;
                    }
                }
                if ((locationValue = (String)locationHeartbeatMap.get(deviceId)) != null) {
                    long ts = Long.parseLong(locationValue);
                    heartbeatInfo.setLocationLastOnlineTimestamp(Long.valueOf(ts));
                    heartbeatInfo.setLocationLastOnlineTime(TimeUtils.objToStr(TimeUtils.timestampToLocalDateTime((long)ts)));
                    if (now - ts < 30000L) {
                        heartbeatInfo.setLocationOnline(true);
                        ++tempLocationOnlineCount;
                    }
                }
                heartbeatInfoList.add(heartbeatInfo);
            }
            if (tempKtOnlineCount > this.maxKtOnline) {
                log.info("update max ktOnline: {}", (Object)tempKtOnlineCount);
                this.maxKtOnline = tempKtOnlineCount;
                this.redisUtils.setValue("vaas:stat:max-kt-on", String.valueOf(this.maxKtOnline));
            }
            if (tempMotionOnlineCount > this.maxMotionOnline) {
                log.info("update max motionOnline: {}", (Object)tempMotionOnlineCount);
                this.maxMotionOnline = tempMotionOnlineCount;
                this.redisUtils.setValue("vaas:stat:max-motion-on", String.valueOf(this.maxMotionOnline));
            }
            if (tempLocationOnlineCount > this.maxLocationOnline) {
                log.info("update max locationOnline: {}", (Object)tempLocationOnlineCount);
                this.maxLocationOnline = tempLocationOnlineCount;
                this.redisUtils.setValue("vaas:stat:max-location-on", String.valueOf(this.maxLocationOnline));
            }
            GetHeartbeatResp newResp = new GetHeartbeatResp();
            newResp.setMaxKtOnline(this.maxKtOnline);
            newResp.setMaxMotionOnline(this.maxMotionOnline);
            newResp.setMaxLocationOnline(this.maxLocationOnline);
            newResp.setCurrentKtOnline(tempKtOnlineCount);
            newResp.setCurrentMotionOnline(tempMotionOnlineCount);
            newResp.setCurrentLocationOnline(tempLocationOnlineCount);
            newResp.setHeartbeatInfoList(heartbeatInfoList);
            this.resp = newResp;
        }
        catch (Exception e) {
            log.error("Heartbeat refresh error", (Throwable)e);
        }
    }

    public GetHeartbeatResp getResp() {
        return this.resp;
    }
}

