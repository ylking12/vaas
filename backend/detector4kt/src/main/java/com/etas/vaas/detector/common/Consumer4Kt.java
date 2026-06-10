/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.component.FleetManagementComponent
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.converter.EventConverter
 *  com.etas.vaas.common.dao.VehicleEventDao
 *  com.etas.vaas.common.dto.KtVehicleEvent
 *  com.etas.vaas.common.enums.SourceType
 *  com.etas.vaas.common.service.VehicleEventService
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  com.etas.vaas.detector.common.Consumer4Kt
 *  com.etas.vaas.detector.config.PostDetectThreadPoolConfig
 *  com.etas.vaas.detector.entity.Frame
 *  com.etas.vaas.detector.entity.FramePackage
 *  com.etas.vaas.detector.entity.FramePackage$RequestData
 *  com.etas.vaas.detector.entity.FramePackage$RequestData$StreamItem
 *  com.etas.vaas.detector.event.bumpy.BumpyProcessor
 *  com.etas.vaas.detector.event.slippery.SlipperyProcessor
 *  com.etas.vaas.detector.utils.StringUtils
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.annotation.Scope
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.detector.common;

import com.etas.vaas.common.component.FleetManagementComponent;
import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.converter.EventConverter;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dto.KtVehicleEvent;
import com.etas.vaas.common.enums.SourceType;
import com.etas.vaas.common.service.VehicleEventService;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import com.etas.vaas.detector.config.PostDetectThreadPoolConfig;
import com.etas.vaas.detector.entity.Frame;
import com.etas.vaas.detector.entity.FramePackage;
import com.etas.vaas.detector.entity.StreamData;
import com.etas.vaas.detector.event.bumpy.BumpyProcessor;
import com.etas.vaas.detector.event.slippery.SlipperyProcessor;
import com.etas.vaas.detector.utils.StringUtils;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/*
 * Exception performing whole class analysis ignored.
 */
@Component
@Scope(value="prototype")
public class Consumer4Kt {
    private static final Logger log = LoggerFactory.getLogger(Consumer4Kt.class);
    @Value(value="${algorithm.kt.bump.enable}")
    private boolean enableBumpyDetection;
    @Value(value="${algorithm.kt.slip.enable}")
    private boolean enableSlipperyDetection;
    @Resource
    private final RedisUtils redisUtils;
    @Resource
    private VehicleEventService vehicleEventService;
    @Resource
    private FleetManagementComponent fleetManagementComponent;
    @Resource
    private SlipperyProcessor slipperyProcessor;
    @Resource
    private BumpyProcessor bumpyProcessor;
    @Resource
    private PostDetectThreadPoolConfig poolConfig;
    private final RedisKeyConfig redisKeyConfig;
    private int bumpyCount = 0;
    private int slipperyCount = 0;

    public void consume(String groupId) {
        log.debug("gid: {}", (Object)groupId);
        String listKey = this.redisKeyConfig.getInstance().getKtQueue() + groupId;
        try {
            long listSize = this.redisUtils.listSize(listKey);
            log.debug("queue size: {}", (Object)listSize);
            if (listSize == 0L) {
                log.debug("queue {}, is empty", (Object)listKey);
                return;
            }
            List listOfFramePackage = this.redisUtils.leftPopWithCount(listKey, listSize);
            List reformattedFrameList = Consumer4Kt.format((List)listOfFramePackage);
        for (Object eachObj : reformattedFrameList) {
                Frame each = (Frame) eachObj;
                KtVehicleEvent bumpyEvent;
                KtVehicleEvent slipperyEvent;
                if (this.enableSlipperyDetection && (slipperyEvent = this.slipperyProcessor.identify(each)).getStatus() == 1) {
                    ++this.slipperyCount;
                    this.addDeviceId(slipperyEvent);
                    VehicleEventDao slipDao = EventConverter.INSTANCE.ktEvent2VehicleEventDao(slipperyEvent);
                    slipDao.setSourceType(SourceType.KT);
                    slipDao.setLevel(Integer.valueOf(3));
                    final VehicleEventDao finalSlipDao = slipDao;
                    this.poolConfig.postDetectionExecutor().execute(() -> this.vehicleEventService.handleEvent(finalSlipDao));
                    log.info("Detected slippery event: {}", (Object)this.slipperyCount);
                }
                if (!this.enableBumpyDetection || (bumpyEvent = this.bumpyProcessor.identify(each)).getStatus() != 1) continue;
                ++this.bumpyCount;
                this.addDeviceId(bumpyEvent);
                VehicleEventDao bumpDao2 = EventConverter.INSTANCE.ktEvent2VehicleEventDao(bumpyEvent);
                    bumpDao2.setSourceType(SourceType.KT);
                    bumpDao2.setLevel(Integer.valueOf(3));
                    final VehicleEventDao finalBumpDao2 = bumpDao2;
                    this.poolConfig.postDetectionExecutor().execute(() -> this.vehicleEventService.handleEvent(finalBumpDao2));
                log.info("Detected bumpy event: {}", (Object)this.bumpyCount);
            }
        }
        catch (Exception e) {
            log.error("Error while consuming message: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    public static List<Frame> format(List<String> stringListFromRedis) {
        ArrayList<Frame> frameList = new ArrayList<Frame>();
        for (String rawFramePackage : stringListFromRedis) {
            log.debug("RawString of KtFramePackage: {}", (Object)rawFramePackage);
            FramePackage deserializedFramePackage = (FramePackage)JsonUtils.toObj((String)rawFramePackage, FramePackage.class);
            log.debug("KtFramePackage: {}", (Object)deserializedFramePackage);
            if (deserializedFramePackage == null) {
                log.error("Error parsing JSON: {}", (Object)rawFramePackage);
                continue;
            }
            List reformattedFrameList = Consumer4Kt.frameDataSetter((FramePackage)deserializedFramePackage);
            frameList.addAll(reformattedFrameList);
        }
        log.debug("KtFrameList: {}", frameList);
        return frameList;
    }

    public static List<Frame> frameDataSetter(FramePackage framePackage) {
        ArrayList<Frame> result = new ArrayList<Frame>();
        for (FramePackage.RequestData eachData : framePackage.getData()) {
            Frame frame = new Frame();
            frame.setReceivedTime(framePackage.getReceivedAt());
            frame.setDate(eachData.getDate());
            frame.setSn(eachData.getSn());
            Map<String, String> streamDataMap = (Map<String, String>) ((java.util.List)eachData.getStreamData()).stream().collect(java.util.stream.Collectors.toMap(FramePackage.RequestData.StreamItem::getName, FramePackage.RequestData.StreamItem::getValue));
            float vehicleSpd = StringUtils.strToFloat(streamDataMap.get("VehicleSpd( km/h)"));
            float steerWheelAngle = StringUtils.strToFloat(streamDataMap.get("SteerWheelAngle( deg)"));
            float lateralAcce = StringUtils.strToFloat(streamDataMap.get("LateralAcce( m/s2)"));
            float longitudeAcc = StringUtils.strToFloat(streamDataMap.get("LongitudeAcc( m/s2)"));
            float escPressure = StringUtils.strToFloat(streamDataMap.get("ESC_Mcylinder_Pressure( bar)"));
            float flWheelSpd = StringUtils.strToFloat(streamDataMap.get("FLWheelSpd( km/h)"));
            float rlWheelSpd = StringUtils.strToFloat(streamDataMap.get("RLWheelSpd( km/h)"));
            float rrWheelSpd = StringUtils.strToFloat(streamDataMap.get("RRWheelSpd( km/h)"));
            float frWheelSpd = StringUtils.strToFloat(streamDataMap.get("FRWheelSpd( km/h)"));
            if (vehicleSpd == Float.MIN_VALUE || steerWheelAngle == Float.MIN_VALUE || lateralAcce == Float.MIN_VALUE || longitudeAcc == Float.MIN_VALUE || escPressure == Float.MIN_VALUE || flWheelSpd == Float.MIN_VALUE || rlWheelSpd == Float.MIN_VALUE || rrWheelSpd == Float.MIN_VALUE || frWheelSpd == Float.MIN_VALUE) continue;
            frame.setVehicleSpd(Float.valueOf(vehicleSpd / 3.6f));
            frame.setWiperFlag(streamDataMap.get("WiperFlag"));
            frame.setSteerWheelAngle(Float.valueOf(steerWheelAngle));
            frame.setLateralAcce(Float.valueOf(lateralAcce));
            frame.setLongitudeAcc(Float.valueOf(longitudeAcc));
            frame.setEscMcylinderPressure(Float.valueOf(escPressure));
            frame.setFlWheelSpd(Float.valueOf(flWheelSpd / 3.6f));
            frame.setRlWheelSpd(Float.valueOf(rlWheelSpd / 3.6f));
            frame.setRrWheelSpd(Float.valueOf(rrWheelSpd / 3.6f));
            frame.setFrWheelSpd(Float.valueOf(frWheelSpd / 3.6f));
            result.add(frame);
        }
        return result;
    }

    private void addDeviceId(KtVehicleEvent event) {
        String deviceId = this.fleetManagementComponent.getDeviceIdBySn(event.getSn());
        event.setDeviceId(deviceId);
    }

    public Consumer4Kt(RedisUtils redisUtils, RedisKeyConfig redisKeyConfig) {
        this.redisUtils = redisUtils;
        this.redisKeyConfig = redisKeyConfig;
    }
}

