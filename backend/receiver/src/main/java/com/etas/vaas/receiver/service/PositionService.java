/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.service.PositionService | STATUS: Restored */
package com.etas.vaas.receiver.service;

import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.converter.LocationConverter;
import com.etas.vaas.common.dto.CachedVehiclePosition;
import com.etas.vaas.common.dto.LocationFrame;
import com.etas.vaas.common.log.DeviceLogger;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.MathUtils;
import com.etas.vaas.common.utils.RedisUtils;
import com.etas.vaas.receiver.config.DumpConfig;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PositionService extends BaseReceiverService {
    private static final Logger log = LoggerFactory.getLogger(PositionService.class);

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private RedisKeyConfig redisKeyConfig;

    @Value("${redis.location-max-queue-size}")
    private Long maxLocationQueueSize;

    @Value("${redis.location-allow-max-overflow}")
    private Long maxLocationOverflowSize;

    @Resource
    private DumpConfig dumpConfig;

    @Resource
    private DeviceLogger deviceLogger;

    private final Map<String, Long> lastSpeedTimestamp = new HashMap<>();
    private long frameCounter;
    private final ConcurrentHashMap<String, Long> heartBeatCountMap = new ConcurrentHashMap<>();

    public PositionService() {
        super(25, "location");
    }

    public void handlePositionData(LocationFrame position) {
        CachedVehiclePosition positionToCache = LocationConverter.INSTANCE
                .locationFrame2CacheVehiclePosition(position);
        int speed = calculateSpeed(position);
        positionToCache.setSpeed(speed);
        String deviceId = position.getDeviceId();
        deviceLogger.debug(deviceId, "cached position: {}", positionToCache);
        log.debug("cached position: {}", positionToCache);
        if (position.getLongitude() == 0.0 || position.getLatitude() == 0.0) {
            return;
        }
        cachePosition(deviceId, positionToCache);
        setHeartbeat(deviceId);
        String cacheLocationString = JsonUtils.toStr(positionToCache);
        DumpConfig.SubDump coordinateDump = dumpConfig.getCoordinate();
        if (coordinateDump.isEnable() && coordinateDump.getDevice().contains(deviceId)) {
            JsonUtils.writeToFile("coordinate-" + LocalDate.now() + ".json", cacheLocationString);
        }
    }

    public void cachePosition(String deviceId, CachedVehiclePosition cachedVehiclePosition) {
        String key = redisKeyConfig.getInstance().getVehicleInfoPrefix() + deviceId;
        if (frameCounter > 100) {
            long listSize = redisUtils.listSize(key);
            if (listSize > maxLocationQueueSize + maxLocationOverflowSize) {
                log.warn("trimming redis list with key : {}, size :{}", key, listSize);
                redisUtils.lTrim(key, -maxLocationQueueSize, -1);
                frameCounter = 0;
            }
        }
        deviceLogger.debug(deviceId, "push location frame to redis key: {} with value: {}",
                key, cachedVehiclePosition);
        log.debug("push location frame to redis key: {} with value: {}", key, cachedVehiclePosition);
        redisUtils.rightPush(key, cachedVehiclePosition);
        frameCounter++;
    }

    private int calculateSpeed(LocationFrame currentFrame) {
        String deviceId = currentFrame.getDeviceId();
        if (!lastSpeedTimestamp.containsKey(deviceId)) {
            lastSpeedTimestamp.put(deviceId, currentFrame.getTimestamp());
        }
        String previousInfoString = redisUtils.getLastItemInList(
                redisKeyConfig.getInstance().getVehicleInfoPrefix() + deviceId);
        log.trace("previousInfoString: {}", previousInfoString);
        if (StringUtils.isBlank(previousInfoString)) {
            log.warn("no previous info in redis for device-{}", deviceId);
            return 0;
        }
        CachedVehiclePosition previousInfo = JsonUtils.toObj(previousInfoString, CachedVehiclePosition.class);
        log.trace("previousInfo: {}", previousInfo);
        if (ObjectUtils.isEmpty(previousInfo)) {
            log.warn("previousInfo for device-{} is null!", deviceId);
            return 0;
        }
        if (ObjectUtils.isEmpty(previousInfo.getSpeed())) {
            log.warn("previous speed for device-{} is null!", deviceId);
            return 0;
        }
        boolean calculateIntervalCondition = currentFrame.getTimestamp() - lastSpeedTimestamp.get(deviceId) > 1000L;
        log.trace("previousInfo.getTimestamp() != null : {}", previousInfo.getTimestamp() != null);
        log.trace("currentFrame.getTimestamp(): {}", currentFrame.getTimestamp());
        log.trace("this.lastSpeedTimestamp.get(deviceId): {}", lastSpeedTimestamp.get(deviceId));
        log.trace("calculateIntervalCondition: {}", calculateIntervalCondition);
        if (previousInfo.getTimestamp() != null && calculateIntervalCondition) {
            double meter = MathUtils.haversineDistance(previousInfo.getLongitude(), previousInfo.getLatitude(),
                    currentFrame.getLongitude(), currentFrame.getLatitude());
            if (meter >= 500.0) {
                log.warn("calSpd gps has wrong! meter={}", meter);
                log.warn("calSpd previous gps lat={},lng={}", previousInfo.getLatitude(), previousInfo.getLongitude());
                log.warn("calSpd current gps lat={},lng={}", currentFrame.getLatitude(), currentFrame.getLongitude());
                return previousInfo.getSpeed();
            }
            double deltaTime = (currentFrame.getTimestamp() - previousInfo.getTimestamp()) / 1000.0;
            int currentSpeed = (int) (meter / deltaTime * 3.6);
            log.trace("calculated current speed: {}", currentSpeed);
            lastSpeedTimestamp.put(deviceId, currentFrame.getTimestamp());
            return currentSpeed;
        }
        log.trace("returning previous speed: {}", previousInfo.getSpeed());
        return previousInfo.getSpeed();
    }

    private void calOnlineTime(String deviceId) {
        heartBeatCountMap.compute(deviceId, (id, count) -> {
            if (count != null && count > 6L) {
                redisUtils.hashIncrementByDouble("online:time", id, 0.5D);
                return count - 6L;
            }
            return count;
        });
    }
}
