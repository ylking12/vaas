/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.service.web.LocationService
 *  com.etas.vaas.backend.vo.OnlineVehicle
 *  com.etas.vaas.backend.vo.OnlineVehicle$Coordinates
 *  com.etas.vaas.common.component.FleetManagementComponent
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.dao.VehicleEventDao
 *  com.etas.vaas.common.dto.CachedVehiclePosition
 *  com.etas.vaas.common.entity.FleetManagement
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.web;

import com.etas.vaas.backend.vo.Coordinates;

import com.etas.vaas.backend.vo.OnlineVehicle;
import com.etas.vaas.common.component.FleetManagementComponent;
import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dto.CachedVehiclePosition;
import com.etas.vaas.common.entity.FleetManagement;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    private static final Logger log = LoggerFactory.getLogger(LocationService.class);
    @Resource
    private FleetManagementComponent fleetManagementComponent;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisKeyConfig redisKeyConfig;

    private Map<String, Integer> getEventCountByDeviceId(String redisKey) {
        Set<String> eventStringSet = this.redisUtils.getAllFromZSet(redisKey);
        if (eventStringSet == null || eventStringSet.isEmpty()) {
            return new HashMap<String, Integer>(0);
        }
        ArrayList<String> eventStringList = new ArrayList(eventStringSet);
        HashMap<String, Integer> outputMap = new HashMap<String, Integer>(eventStringList.size());
        for (String eventJson : eventStringList) {
            try {
                VehicleEventDao event = (VehicleEventDao)JsonUtils.parseJson((String)eventJson, VehicleEventDao.class);
                String deviceId = event.getDeviceId();
                outputMap.merge(deviceId, 1, Integer::sum);
            }
            catch (Exception e) {
                log.error("\u89e3\u6790\u4e8b\u4ef6JSON\u5931\u8d25\uff0c\u6570\u636e: {}", eventJson, e);
            }
        }
        return outputMap;
    }

    public Map<String, Integer> getBumpEventCountByDeviceId() {
        return this.getEventCountByDeviceId(this.redisKeyConfig.getInstance().getBumpEventKey());
    }

    public Map<String, Integer> getSlipEventCountByDeviceId() {
        return this.getEventCountByDeviceId(this.redisKeyConfig.getInstance().getSlipEventKey());
    }

    public Map<String, OnlineVehicle> getOnlineVehicles() {
        ArrayList<String> deviceIdToTrack = new ArrayList(this.fleetManagementComponent.getDeviceId2CarMap().keySet());
        HashMap<String, OnlineVehicle> outputMap = new HashMap<String, OnlineVehicle>();
        for (String deviceId : deviceIdToTrack) {
            String vehicleLocationKeyName = this.redisKeyConfig.getInstance().getVehicleInfoPrefix() + deviceId;
            String vehicleLocationInfo = this.redisUtils.getLastItemInList(vehicleLocationKeyName);
            if (vehicleLocationInfo == null) continue;
            CachedVehiclePosition cachedVehiclePosition = (CachedVehiclePosition)JsonUtils.parseJson((String)vehicleLocationInfo, CachedVehiclePosition.class);
            long lastOnlineTimestamp = cachedVehiclePosition.getTimestamp();
            long currentTimestamp = System.currentTimeMillis();
            if (currentTimestamp - lastOnlineTimestamp >= 60000L) continue;
            OnlineVehicle onlineVehicle = new OnlineVehicle();
            String plateNumber = ((FleetManagement)this.fleetManagementComponent.getDeviceId2CarMap().get(deviceId)).getPlate();
            if (plateNumber != null) {
                String maskedPlateNumber = plateNumber.substring(0, plateNumber.length() - 3) + "***";
                onlineVehicle.setPlateNumber(maskedPlateNumber);
            } else {
                log.debug("deviceId has no plate number: {}", deviceId);
                onlineVehicle.setPlateNumber("\u82cfB ****");
            }
            onlineVehicle.setDeviceId(deviceId);
            onlineVehicle.setSerialNumber(((FleetManagement)this.fleetManagementComponent.getDeviceId2CarMap().get(deviceId)).getKt710Id());
            onlineVehicle.setVehicleId(deviceId);
            onlineVehicle.setSpeed(cachedVehiclePosition.getSpeed());
            onlineVehicle.setEventCount(Integer.valueOf(0));
            onlineVehicle.setCoordinates(new Coordinates(cachedVehiclePosition.getLongitude(), cachedVehiclePosition.getLatitude()));
            outputMap.put(deviceId, onlineVehicle);
        }
        return outputMap;
    }

    public LocationService() {
    }
}

