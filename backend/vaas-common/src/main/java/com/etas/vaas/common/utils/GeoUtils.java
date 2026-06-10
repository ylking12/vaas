/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.data.geo.Circle
 *  org.springframework.data.geo.Distance
 *  org.springframework.data.geo.GeoResult
 *  org.springframework.data.geo.GeoResults
 *  org.springframework.data.geo.Metric
 *  org.springframework.data.geo.Point
 *  org.springframework.data.redis.connection.RedisGeoCommands$DistanceUnit
 *  org.springframework.data.redis.connection.RedisGeoCommands$GeoLocation
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.common.utils;

import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dto.CachedVehiclePosition;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.exception.EventError;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.MathUtils;
import com.etas.vaas.common.utils.RedisUtils;
import com.etas.vaas.common.utils.TimeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

@Service
public class GeoUtils {
    private static final Logger log = LoggerFactory.getLogger(GeoUtils.class);
    @Value(value="${common.geo.eventBounds.longitude.left:120.05}")
    private double longitudeLeft;
    @Value(value="${common.geo.eventBounds.longitude.right:120.60}")
    private double longitudeRight;
    @Value(value="${common.geo.eventBounds.latitude.bottom:31.36}")
    private double latitudeBottom;
    @Value(value="${common.geo.eventBounds.latitude.top:31.73}")
    private double latitudeTop;
    @Value(value="${common.geo.roadNameSearchRange:10}")
    private float roadNameSearchRange;
    @Value(value="${common.geo.eventDeduplicationRange:5}")
    private float eventDeduplicationRange;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisKeyConfig redisKeyConfig;

    @PostConstruct
    public void init() {
        log.info("common.geo.eventBounds.longitude.left : {}", this.longitudeLeft);
        log.info("common.geo.eventBounds.longitude.right : {}", this.longitudeRight);
        log.info("common.geo.eventBounds.latitude.bottom : {}", this.latitudeBottom);
        log.info("common.geo.eventBounds.latitude.top : {}", this.latitudeTop);
        log.info("common.geo.roadNameSearchRange : {}", Float.valueOf(this.roadNameSearchRange));
        log.info("common.geo.eventDeduplicationRange:{}", Float.valueOf(this.eventDeduplicationRange));
    }

    public void setRoadName(VehicleEventDao event) {
        Point centerPoint = new Point(event.getLongitude().doubleValue(), event.getLatitude().doubleValue());
        Distance distance = new Distance((double)this.roadNameSearchRange, (Metric)RedisGeoCommands.DistanceUnit.KILOMETERS);
        Circle circle = new Circle(centerPoint, distance);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = this.redisUtils.findByRadius(this.redisKeyConfig.getInstance().getRoadSegmentCoordinatesKey(), circle);
        if (results != null && !results.getContent().isEmpty()) {
            ArrayList<GeoResult> sortedLocations = new ArrayList<GeoResult>(results.getContent());
            sortedLocations.sort(Comparator.comparingDouble(result -> result.getDistance().getValue()));
            String roadName = ((String)((RedisGeoCommands.GeoLocation)((GeoResult)sortedLocations.get(0)).getContent()).getName()).split(":")[0];
            event.setRoadName(roadName != null ? roadName : "\u672a\u5b9a\u4e49\u8def\u540d");
        } else {
            event.setRoadName("\u672a\u5b9a\u4e49\u8def\u540d");
        }
    }

    public String findRoadNameByCoordinates(double longitude, double latitude) {
        Point centerPoint = new Point(longitude, latitude);
        Distance distance = new Distance((double)this.roadNameSearchRange, (Metric)RedisGeoCommands.DistanceUnit.KILOMETERS);
        Circle circle = new Circle(centerPoint, distance);
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = this.redisUtils.findByRadius(this.redisKeyConfig.getInstance().getRoadSegmentCoordinatesKey(), circle);
        if (results != null && !results.getContent().isEmpty()) {
            ArrayList<GeoResult> sortedLocations = new ArrayList<GeoResult>(results.getContent());
            sortedLocations.sort(Comparator.comparingDouble(result -> result.getDistance().getValue()));
            String roadName = ((String)((RedisGeoCommands.GeoLocation)((GeoResult)sortedLocations.get(0)).getContent()).getName()).split(":")[0];
            return roadName != null ? roadName : "\u672a\u5b9a\u4e49\u8def\u540d";
        }
        return "\u672a\u5b9a\u4e49\u8def\u540d";
    }

    public boolean isInsideArea(double longitude, double latitude) {
        log.debug("longitudeLeft: {},longitudeRight: {},latitudeBottom: {},latitudeTop: {}", new Object[]{this.longitudeLeft, this.longitudeRight, this.latitudeBottom, this.latitudeTop});
        return longitude >= this.longitudeLeft && longitude <= this.longitudeRight && latitude >= this.latitudeBottom && latitude <= this.latitudeTop;
    }

    public boolean checkArea(VehicleEventDao event) {
        log.debug("longitudeLeft: {},longitudeRight: {},latitudeBottom: {},latitudeTop: {}", new Object[]{this.longitudeLeft, this.longitudeRight, this.latitudeBottom, this.latitudeTop});
        if (event.getLongitude() >= this.longitudeLeft && event.getLongitude() <= this.longitudeRight && event.getLatitude() >= this.latitudeBottom && event.getLatitude() <= this.latitudeTop) {
            event.setInArea(true);
            return true;
        }
        event.setInArea(false);
        return false;
    }

    public void setDuplicateInfo(VehicleEventDao event) {
        String key = event.getEventType().equals((Object)EventType.BUMP) ? this.redisKeyConfig.getInstance().getBumpEventKey() : this.redisKeyConfig.getInstance().getSlipEventKey();
        Set<String> bumpyHistory = this.redisUtils.getAllFromZSet(key);
        for (String eachStr : bumpyHistory) {
            VehicleEventDao eachObj = JsonUtils.toObj(eachStr, VehicleEventDao.class);
            if (ObjectUtils.isEmpty((Object)eachObj) || ObjectUtils.isEmpty((Object)eachObj.getLongitude()) || eachObj.getLatitude() == null) {
                throw new IllegalArgumentException("history event is null or don't have coordinate");
            }
            double distance = MathUtils.haversineDistance(event.getLongitude(), event.getLatitude(), eachObj.getLongitude(), eachObj.getLatitude());
            if (!(distance <= (double)this.eventDeduplicationRange)) continue;
            event.setDuplicated(true);
            log.info("Duplicated event found: {}", event);
            return;
        }
        event.setDuplicated(false);
    }

    public boolean checkDuplication(VehicleEventDao event) {
        String key = event.getEventType().equals((Object)EventType.BUMP) ? this.redisKeyConfig.getInstance().getBumpEventKey() : this.redisKeyConfig.getInstance().getSlipEventKey();
        Set<String> bumpyHistory = this.redisUtils.getAllFromZSet(key);
        for (String eachStr : bumpyHistory) {
            VehicleEventDao eventInCache = JsonUtils.toObj(eachStr, VehicleEventDao.class);
            if (eventInCache == null || eventInCache.getCellAddress() == null || !Objects.equals(eventInCache.getCellAddress(), event.getCellAddress())) continue;
            event.setDuplicated(true);
            return true;
        }
        event.setDuplicated(false);
        return false;
    }

    public List<CachedVehiclePosition> getAllCachedVehicleLocation(String deviceId) {
        String key = this.redisKeyConfig.getInstance().getVehicleInfoPrefix() + deviceId;
        List<String> cacheStringList = this.redisUtils.lRange(key, 0L, -1L);
        if (ObjectUtils.isEmpty(cacheStringList)) {
            log.error("No GPS data found for deviceId: {}", deviceId);
            return Collections.emptyList();
        }
        ArrayList<CachedVehiclePosition> cachedVehiclePositionList = new ArrayList<CachedVehiclePosition>();
        for (String each : cacheStringList) {
            cachedVehiclePositionList.add(JsonUtils.toObj(each, CachedVehiclePosition.class));
        }
        return cachedVehiclePositionList;
    }

    public void checkEventTimeWithinCacheTime(List<CachedVehiclePosition> cachedVehiclePositionList, VehicleEventDao cachedEventDto) {
        long eventTimestamp = cachedEventDto.getEventTimestamp();
        long minGpsTimestamp = cachedVehiclePositionList.get(0).getTimestamp();
        long maxGpsTimestamp = cachedVehiclePositionList.get(cachedVehiclePositionList.size() - 1).getTimestamp();
        if (minGpsTimestamp > eventTimestamp) {
            log.warn("min gps timestamp: {} {}, event timestamp: {} {}, diff: {}", new Object[]{minGpsTimestamp, TimeUtils.timestampToLocalDateTime(minGpsTimestamp), eventTimestamp, TimeUtils.timestampToLocalDateTime(eventTimestamp), eventTimestamp - minGpsTimestamp});
            throw new EventError.NoGpsError("event-time earlier than min gps-time, drop event");
        }
        if (maxGpsTimestamp < eventTimestamp) {
            log.warn("max gps timestamp: {} {}, event timestamp: {} {}, diff: {}", new Object[]{maxGpsTimestamp, TimeUtils.timestampToLocalDateTime(maxGpsTimestamp), eventTimestamp, TimeUtils.timestampToLocalDateTime(eventTimestamp), eventTimestamp - maxGpsTimestamp});
            throw new EventError.NoGpsError("event-time later than max gps-time, drop event");
        }
    }
}

