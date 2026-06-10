/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.uber.h3core.H3Core
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.common.service;

import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.converter.EventConverter;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dto.CachedVehiclePosition;
import com.etas.vaas.common.entity.Event;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.enums.SourceType;
import com.etas.vaas.common.exception.EventError;
import com.etas.vaas.common.mapper.EventMapper;
import com.etas.vaas.common.utils.GeoUtils;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.MathUtils;
import com.etas.vaas.common.utils.NanoIdGenerator;
import com.etas.vaas.common.utils.RedisUtils;
import com.etas.vaas.common.utils.TimeUtils;
import com.uber.h3core.H3Core;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VehicleEventService {
    private static final Logger log = LoggerFactory.getLogger(VehicleEventService.class);
    @Resource
    private GeoUtils geoUtils;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private EventMapper eventMapper;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Resource
    private H3Core h3Core;
    @Value(value="${common.locationSearch.toleratedDeltaTime:1500}")
    private long toleratedDeltaTime;
    @Value(value="${common.geo.eventDeduplicationEnabled:true}")
    private boolean eventDeduplicationEnabled;
    @Value(value="${common.geo.eventDeduplicationH3Resolution:13}")
    private int eventDeduplicationH3Resolution;

    @PostConstruct
    public void init() {
        log.info("common.locationSearch.toleratedDeltaTime: {}", this.toleratedDeltaTime);
        log.info("common.geo.eventDeduplicationEnabled:{}", this.eventDeduplicationEnabled);
        log.info("common.geo.eventDeduplicationH3Resolution: {}", this.eventDeduplicationH3Resolution);
    }

    public void addCoordinatesToEvent(VehicleEventDao event) {
        List<CachedVehiclePosition> cachedLocationList = this.geoUtils.getAllCachedVehicleLocation(event.getDeviceId());
        ArrayList<Long> cachedLocationTimestamps = new ArrayList<Long>();
        for (CachedVehiclePosition location : cachedLocationList) {
            cachedLocationTimestamps.add(location.getTimestamp());
        }
        int mostMatchIndex = MathUtils.binarySearchForTimestampIndex(cachedLocationTimestamps, event.getEventTimestamp());
        long mostMatchTimestamp = (Long)cachedLocationTimestamps.get(mostMatchIndex);
        long absolutDeltaTime = Math.abs(mostMatchTimestamp - event.getEventTimestamp());
        if (absolutDeltaTime > this.toleratedDeltaTime) {
            log.warn("most match timestamp \u4e0e event timestamp \u7684\u65f6\u95f4\u5dee {} \u5927\u4e8e\u5bb9\u5fcd\u503c {}", absolutDeltaTime, (Object)this.toleratedDeltaTime);
            log.warn("event time: {}, most match time: {}", TimeUtils.timestampToLocalDateTime(event.getEventTimestamp()), (Object)TimeUtils.timestampToLocalDateTime(mostMatchTimestamp));
            long minCachedLocationTimestamp = (Long)cachedLocationTimestamps.get(0);
            long maxCachedLocationTimestamp = (Long)cachedLocationTimestamps.get(cachedLocationTimestamps.size() - 1);
            long eventTimestamp = event.getEventTimestamp();
            if (minCachedLocationTimestamp > eventTimestamp) {
                log.warn("min gps timestamp: {} {}, event timestamp: {} {}, diff: {}", new Object[]{minCachedLocationTimestamp, TimeUtils.timestampToLocalDateTime(minCachedLocationTimestamp), eventTimestamp, TimeUtils.timestampToLocalDateTime(eventTimestamp), eventTimestamp - minCachedLocationTimestamp});
            }
            if (maxCachedLocationTimestamp < eventTimestamp) {
                log.warn("max gps timestamp: {} {}, event timestamp: {} {}, diff: {}", new Object[]{maxCachedLocationTimestamp, TimeUtils.timestampToLocalDateTime(maxCachedLocationTimestamp), eventTimestamp, TimeUtils.timestampToLocalDateTime(eventTimestamp), eventTimestamp - maxCachedLocationTimestamp});
            }
            throw new EventError.ClosestLocationNotFoundError("deltaTime > tolerated time, drop event");
        }
        double longitude = cachedLocationList.get(mostMatchIndex).getLongitude();
        double latitude = cachedLocationList.get(mostMatchIndex).getLatitude();
        event.setLongitude(longitude);
        event.setLatitude(latitude);
    }

    public void handleEvent(VehicleEventDao event) {
        this.addEventIdToEvent(event);
        this.addCoordinatesToEvent(event);
        this.addH3CellAddressToEvent(event);
        boolean inValidArea = this.geoUtils.checkArea(event);
        if (!inValidArea) {
            log.info("detected an event outside valid area: {}", event);
            return;
        }
        boolean duplicated = this.geoUtils.checkDuplication(event);
        if (!duplicated) {
            String roadName = this.geoUtils.findRoadNameByCoordinates(event.getLongitude(), event.getLatitude());
            event.setRoadName(roadName);
            String eventContent = this.serializeEvent(event);
            this.cacheEvent(event, eventContent);
            this.publishEvent(eventContent);
            this.persistEvent(event);
        } else {
            log.info("detected a duplicated event: {}", event);
        }
    }

    public void addEventIdToEvent(VehicleEventDao event) {
        event.setEventId(NanoIdGenerator.nextId());
    }

    public void addH3CellAddressToEvent(VehicleEventDao event) {
        event.setCellAddress(this.h3Core.latLngToCellAddress(event.getLatitude().doubleValue(), event.getLongitude().doubleValue(), this.eventDeduplicationH3Resolution));
    }

    public void addRoadNameToEvent(VehicleEventDao event) {
        String roadName = this.geoUtils.findRoadNameByCoordinates(event.getLongitude(), event.getLatitude());
        event.setRoadName(roadName);
    }

    public void cacheEvent(VehicleEventDao event, String eventContent) {
        long now = System.currentTimeMillis();
        switch (event.getEventType()) {
            case BUMP: {
                this.redisUtils.addToZSet(this.redisKeyConfig.getInstance().getBumpEventKey(), eventContent, now);
                this.redisUtils.hashIncrementByInt(this.redisKeyConfig.getInstance().getBumpCounterKey(), event.getDeviceId(), 1L);
                break;
            }
            case SLIP: {
                this.redisUtils.addToZSet(this.redisKeyConfig.getInstance().getSlipEventKey(), eventContent, now);
                this.redisUtils.hashIncrementByInt(this.redisKeyConfig.getInstance().getSlipCounterKey(), event.getDeviceId(), 1L);
                break;
            }
            case PONDING: {
                this.redisUtils.addToZSet(this.redisKeyConfig.getInstance().getPondingEventKey(), eventContent, now);
                break;
            }
            default: {
                throw new IllegalStateException("unknown event type!!!!");
            }
        }
    }

    public String serializeEvent(VehicleEventDao event) {
        return JsonUtils.toStr(event);
    }

    public void publishEvent(String eventContent) {
        log.info("publishing to {} with message {}", this.redisKeyConfig.getInstance().getEventTopic(), (Object)eventContent);
        this.redisUtils.publishMessage(this.redisKeyConfig.getInstance().getEventTopic(), eventContent);
    }

    public void persistEvent(VehicleEventDao event) {
        Event newEvent = EventConverter.INSTANCE.vehicleEventDao2Event(event);
        this.eventMapper.insert(newEvent);
        log.info("write event to db: {}", newEvent);
    }

    public void processWeatherSensorEvent(String roadName, EventType eventType) {
        LocalDateTime currentTime;
        Coordinate coordinate = this.getRoadCoordinate(roadName);
        double longitude = coordinate.longitude();
        double latitude = coordinate.latitude();
        Optional<Event> latestEvent = this.eventMapper.getLatestPondingEventByRoadName(roadName, eventType.getTypeString());
        if (latestEvent.isEmpty()) {
            log.info("\u9053\u8def[{}]\u65e0\u5386\u53f2\u4e8b\u4ef6\uff0c\u76f4\u63a5\u53d1\u5e03\u65b0\u4e8b\u4ef6", roadName);
            this.createAndPublishEvent(roadName, longitude, latitude, eventType);
            return;
        }
        LocalDateTime lastEventTime = latestEvent.get().getEventTime();
        long intervalMinutes = ChronoUnit.MINUTES.between(lastEventTime, currentTime = LocalDateTime.now());
        if (intervalMinutes > 15L) {
            log.info("\u9053\u8def[{}]\u6700\u8fd1\u4e8b\u4ef6\u8ddd\u4eca[{}]\u5206\u949f(>15)\uff0c\u53d1\u5e03\u65b0\u4e8b\u4ef6", (Object)roadName, (Object)intervalMinutes);
            this.createAndPublishEvent(roadName, longitude, latitude, eventType);
        } else {
            log.info("\u9053\u8def[{}]\u6700\u8fd1\u4e8b\u4ef6\u8ddd\u4eca[{}]\u5206\u949f(\u226415)\uff0c\u8df3\u8fc7\u53d1\u5e03", (Object)roadName, (Object)intervalMinutes);
        }
    }

    private Coordinate getRoadCoordinate(String roadName) {
        HashMap<String, Coordinate> roadCoordinates = new HashMap<String, Coordinate>(){
            {
                this.put("\u6587\u60e0\u8def\u4e0e\u9526\u7ee3\u8def", new Coordinate(120.29628, 31.68117));
                this.put("\u8d21\u6e56\u5927\u9053\u4e0e\u91d1\u57ce\u8def\u53e3", new Coordinate(120.4279, 31.5848));
                this.put("\u8fd0\u6cb3\u897f\u8def", new Coordinate(120.281, 31.566));
                this.put("\u673a\u573a\u8def-\u6cf0\u5c71\u8def", new Coordinate(120.3707, 31.541));
            }
        };
        return roadCoordinates.getOrDefault(roadName, new Coordinate(120.32034, 31.5017));
    }

    private void createAndPublishEvent(String roadName, double longitude, double latitude, EventType eventType) {
        long currentTimestamp = System.currentTimeMillis();
        VehicleEventDao event = this.buildWeatherSensorEvent(roadName, longitude, latitude, eventType, currentTimestamp);
        String eventContent = this.serializeEvent(event);
        this.cacheEvent(event, eventContent);
        this.publishEvent(eventContent);
        this.persistEvent(event);
    }

    private VehicleEventDao buildWeatherSensorEvent(String roadName, double longitude, double latitude, EventType eventType, long timestamp) {
        VehicleEventDao event = new VehicleEventDao();
        event.setStatus(1);
        event.setEventId(NanoIdGenerator.nextId());
        event.setEventType(eventType);
        event.setSourceType(SourceType.WEATHER_SENSOR);
        event.setLevel(3);
        event.setEventTimestamp(timestamp);
        event.setPerceptionTimestamp(timestamp);
        event.setReceivedTimestamp(timestamp);
        event.setInArea(true);
        event.setDuplicated(false);
        event.setSimulated(false);
        event.setRoadName(roadName);
        event.setLongitude(longitude);
        event.setLatitude(latitude);
        event.setCellAddress(this.h3Core.latLngToCellAddress(latitude, longitude, this.eventDeduplicationH3Resolution));
        return event;
    }

    public VehicleEventService() {
    }

    private record Coordinate(double longitude, double latitude) {
    }
}

