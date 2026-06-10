/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.baomidou.mybatisplus.core.toolkit.support.SFunction
 *  io.micrometer.common.util.StringUtils
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.common.component;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.etas.vaas.common.entity.FleetManagement;
import com.etas.vaas.common.exception.VehicleError;
import com.etas.vaas.common.mapper.FleetManagementMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FleetManagementComponent {
    private static final Logger log = LoggerFactory.getLogger(FleetManagementComponent.class);
    private final ConcurrentHashMap<String, FleetManagement> sn2CarMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, FleetManagement> deviceId2CarMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, Integer> ktCarsGroupMap = new ConcurrentHashMap();
    private final ConcurrentHashMap<String, Integer> motionCarsGroupMap = new ConcurrentHashMap();
    private final Set<String> bumpEnabledDeviceSet = ConcurrentHashMap.newKeySet();
    private final Set<String> slipEnabledDeviceSet = ConcurrentHashMap.newKeySet();
    @Resource
    private FleetManagementMapper fleetManagementMapper;

    @PostConstruct
    public void init() {
        log.info("FleetManagementBean Init...");
        this.refreshFleetManagement();
    }

    @Scheduled(cron="0 */5 * * * *")
    public void scheduledTask() {
        this.refreshFleetManagement();
    }

    private void refreshFleetManagement() {
        this.setSn2CarMapAndDeviceId2CarMap();
        this.setKtCarsGroupMap();
        this.setMotionCarsGroupMap();
        this.setBumpEnabledDeviceSet();
        this.setSlipEnabledDeviceSet();
    }

    public String getDeviceIdBySn(String sn) {
        String deviceId = this.sn2CarMap.get(sn).getImei();
        if (ObjectUtils.isEmpty((Object)deviceId)) {
            log.error("Car mapping not found for SN: {}", sn);
            throw new VehicleError.NoDeviceIdError("no DeviceId found for sn");
        }
        return deviceId;
    }

    private void setSn2CarMapAndDeviceId2CarMap() {
        log.info("Reading car FleetManagements from MySQL");
        List<FleetManagement> fleetManagementList = this.fleetManagementMapper.selectList(null);
        for (FleetManagement each : fleetManagementList) {
            this.sn2CarMap.put(each.getKt710Id(), each);
            this.deviceId2CarMap.put(each.getImei(), each);
        }
        log.debug("sn2CarMap: {}", this.sn2CarMap);
        log.debug("deviceIdCarMap: {}", this.deviceId2CarMap);
    }

    private void setKtCarsGroupMap() {
        LambdaQueryWrapper<FleetManagement> ktGroupWrapper = new LambdaQueryWrapper<FleetManagement>();
        ktGroupWrapper.eq(FleetManagement::isSlipEnable, (Object)true);
        List<FleetManagement> fleetManagementList = this.fleetManagementMapper.selectList((Wrapper)ktGroupWrapper);
        for (FleetManagement each : fleetManagementList) {
            this.ktCarsGroupMap.put(each.getKt710Id(), each.getGroupId());
        }
        log.debug("ktCarsGroupMap: {}", this.ktCarsGroupMap);
    }

    private void setMotionCarsGroupMap() {
        LambdaQueryWrapper<FleetManagement> motionGroupWrapper = new LambdaQueryWrapper<FleetManagement>();
        motionGroupWrapper.eq(FleetManagement::isBumpEnable, (Object)true);
        List<FleetManagement> fleetManagementList = this.fleetManagementMapper.selectList((Wrapper)motionGroupWrapper);
        for (FleetManagement each : fleetManagementList) {
            this.motionCarsGroupMap.put(each.getImei(), each.getGroupId());
        }
        log.debug("motionCarsGroupMap{}", this.motionCarsGroupMap);
    }

    private void setBumpEnabledDeviceSet() {
        LambdaQueryWrapper<FleetManagement> wrapper = new LambdaQueryWrapper<FleetManagement>();
        wrapper.select(FleetManagement::getImei);
        wrapper.eq(FleetManagement::isBumpEnable, true);
        List<Object> deviceIdList = this.fleetManagementMapper.selectObjs(wrapper);
        this.bumpEnabledDeviceSet.clear();
        for (Object id : deviceIdList) {
            if (id != null) this.bumpEnabledDeviceSet.add(id.toString());
        }
        log.debug("bumpEnabledDeviceSet: {}", this.bumpEnabledDeviceSet);
    }

    private void setSlipEnabledDeviceSet() {
        LambdaQueryWrapper<FleetManagement> wrapper = new LambdaQueryWrapper<FleetManagement>();
        wrapper.select(FleetManagement::getImei);
        wrapper.eq(FleetManagement::isSlipEnable, true);
        List<Object> deviceIdList = this.fleetManagementMapper.selectObjs(wrapper);
        this.slipEnabledDeviceSet.clear();
        for (Object id : deviceIdList) {
            if (id != null) this.slipEnabledDeviceSet.add(id.toString());
        }
        log.debug("slipEnabledDeviceSet: {}", this.slipEnabledDeviceSet);
    }

    public boolean snIllegal(String sn) {
        return StringUtils.isBlank((String)sn) || !this.sn2CarMap.containsKey(sn);
    }

    public boolean deviceIdIllegal(String deviceId) {
        log.debug("this deviceId2CarMap : {}", this.deviceId2CarMap);
        return StringUtils.isBlank((String)deviceId) || !this.deviceId2CarMap.containsKey(deviceId);
    }

    public int getKtGroupId(String sn) {
        return this.ktCarsGroupMap.get(sn);
    }

    public int getMotionGroupId(String deviceId) {
        return this.motionCarsGroupMap.get(deviceId);
    }

    public boolean isKtCar(String sn) {
        return this.ktCarsGroupMap.containsKey(sn);
    }

    public boolean isMotionCar(String deviceId) {
        return this.motionCarsGroupMap.containsKey(deviceId);
    }

    public boolean isBumpCar(String deviceId) {
        return this.bumpEnabledDeviceSet.contains(deviceId);
    }

    public boolean isSlipCar(String deviceId) {
        return this.slipEnabledDeviceSet.contains(deviceId);
    }

    public FleetManagementComponent() {
    }

    public ConcurrentHashMap<String, FleetManagement> getDeviceId2CarMap() {
        return this.deviceId2CarMap;
    }
}

