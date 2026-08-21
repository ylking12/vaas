/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.api.IServiceController
 *  com.etas.vaas.backend.api.IWeatherSensorService
 *  com.etas.vaas.backend.configuration.SensorConfig
 *  com.etas.vaas.backend.configuration.SensorConfig$CompareType
 *  com.etas.vaas.backend.configuration.SensorConfig$EventCheckLogic
 *  com.etas.vaas.backend.configuration.SensorConfig$SaveData
 *  com.etas.vaas.backend.configuration.SensorConfig$SensorInfo
 *  com.etas.vaas.backend.entity.SensorNodeData
 *  com.etas.vaas.backend.entity.SensorNodeDataEntity
 *  com.etas.vaas.backend.service.roadside.WeatherSensorService
 *  com.etas.vaas.backend.service.roadside.WeatherSensorService$1
 *  com.etas.vaas.backend.utils.RedisKeyUtils
 *  com.etas.vaas.common.enums.EventType
 *  com.etas.vaas.common.service.VehicleEventService
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.ApplicationContext
 *  org.springframework.data.redis.core.StringRedisTemplate
 *  org.springframework.data.redis.core.ZSetOperations$TypedTuple
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.roadside;

import com.etas.vaas.backend.api.IServiceController;
import com.etas.vaas.backend.api.IWeatherSensorService;
import com.etas.vaas.backend.configuration.SensorConfig;
import com.etas.vaas.backend.entity.SensorNodeData;
import com.etas.vaas.backend.entity.SensorNodeDataEntity;
import com.etas.vaas.backend.service.roadside.WeatherSensorService;
import com.etas.vaas.backend.utils.RedisKeyUtils;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.service.VehicleEventService;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
public class WeatherSensorService
implements IWeatherSensorService {
    private static final Logger log = LoggerFactory.getLogger(WeatherSensorService.class);
    @Autowired
    SensorConfig sensorConfig;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    private RedisKeyUtils redisKeyUtils;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private VehicleEventService vehicleEventService;

    public void handlerSensorData(SensorNodeDataEntity nodeDataEntity) {
        log.debug("handlerSensorData start");
        Map measurements = this.buildMeasurements(nodeDataEntity);
        log.info("measurements: {}", measurements);
        this.handleDataInRedis(measurements);
        this.eventCheck(measurements);
    }

    public void eventCheck(Map<String, Object> measurements) {
        if (measurements.containsKey("pondingDepth") && measurements.containsKey("RoadName") && measurements.containsKey("Longitude") && measurements.containsKey("Latitude")) {
            log.info("RoadName: {}, pondingDepth:{}", measurements.get("RoadName"), measurements.get("pondingDepth"));
            if (measurements.get("pondingDepth") instanceof Number) {
                float pondingDepth = ((Number)measurements.get("pondingDepth")).floatValue();
                String roadName = measurements.get("RoadName").toString();
                if (pondingDepth >= 5.0f) {
                    this.vehicleEventService.processWeatherSensorEvent(roadName, EventType.PONDING);
                }
            }
        }
    }

    private Map<String, Object> buildMeasurements(SensorNodeDataEntity nodeDataEntity) {
        HashMap<String, Object> measurements = new HashMap<String, Object>();
        measurements.put("SensorId", nodeDataEntity.getSensorId());
        measurements.put("SensorType", nodeDataEntity.getSensorType());
        measurements.put("CoordinateType", nodeDataEntity.getCoordinateType());
        measurements.put("Latitude", nodeDataEntity.getLatitude());
        measurements.put("Longitude", nodeDataEntity.getLongitude());
        measurements.put("RoadName", ((SensorConfig.SensorInfo)this.sensorConfig.getSensorIds().get(String.valueOf(nodeDataEntity.getSensorId()))).getRoadName());
        Map<String, SensorConfig.SaveData> dataConf = (Map<String, SensorConfig.SaveData>)this.sensorConfig.getData().get(nodeDataEntity.getSensorType().name());
        log.debug("find {},{},{} ", nodeDataEntity.getSensorType(), dataConf, dataConf.isEmpty());
        int datasize = nodeDataEntity.getNodeList().size();
        if (!dataConf.isEmpty()) {
            log.debug("start store data by configure {},{}", datasize, dataConf.size());
            dataConf.forEach((k, v) -> {
                if (v.getIndex() - 1 >= datasize) {
                    log.debug("current nodelist size {}, configured index {}, skip", datasize, v.getIndex());
                    return;
                }
                SensorNodeData node = (SensorNodeData)nodeDataEntity.getNodeList().get(v.getIndex() - 1);
                this.storeDataInMap(measurements, v, node);
            });
        }
        return measurements;
    }

    public void startSDK() {
        log.info("applicationContext is null? {}", (this.applicationContext == null ? 1 : 0));
        log.info("WeatherSensor post constructor:{}", this.sensorConfig.isEnabled());
        if (this.sensorConfig.isEnabled()) {
            log.info("WeatherSensor is enabled");
            Map<String, IServiceController> beanMap = this.applicationContext.getBeansOfType(IServiceController.class);
            log.info("Found IServiceController beans: {}", beanMap.keySet());
            Map<String, SensorConfig.SdkInfo> sdks = this.sensorConfig.getSdks();
            sdks.forEach((k, v) -> {
                if (v.isEnabled()) {
                    log.info("create sdk:{}", k);
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        String json = mapper.writeValueAsString(v);
                        log.info("config:{}", json);
                    }
                    catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                    }
                    IServiceController controller = (IServiceController)beanMap.get(v.getClassName());
                    controller.start(v);
                }
            });
        }
    }

    private Map<String, Object> formEventFromMeasurements(Map<String, Object> measurements) {
        List<String> retainedKeys = Arrays.asList("SensorId", "SensorType", "CoordinateType", "Latitude", "Longitude", "RoadName", "RecordTime", "TimeStamp");
        Map<String, Object> event = measurements.entrySet().stream().filter(entry -> retainedKeys.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return event;
    }

    private boolean eventTriggered(Map<String, Object> measurements, SensorConfig.EventCheckLogic event) {
        String dataName = event.getKey();
        Object data = measurements.get(dataName);
        String thresholdStr = event.getValue();
        SensorConfig.CompareType compareType = event.getCompareType();
        switch (event.getDataType()) {
            case Float: {
                float dataValue = ((Float)data).floatValue();
                float threshold = Float.parseFloat(thresholdStr);
                return this.compare(compareType, (Comparable)Float.valueOf(dataValue), (Comparable)Float.valueOf(threshold));
            }
            case UNSigned32INT: {
                long dataValue = (Long)data;
                long threshold = Long.parseLong(thresholdStr);
                return this.compare(compareType, (Comparable)Long.valueOf(dataValue), (Comparable)Long.valueOf(threshold));
            }
        }
        return false;
    }

    private <T extends Comparable<T>> boolean compare(SensorConfig.CompareType type, T data, T threshold) {
        switch (type) {
            case GreatAndEq:
                return data.compareTo(threshold) >= 0;
            case LessAndEq:
                return data.compareTo(threshold) <= 0;
            case Great:
                return data.compareTo(threshold) > 0;
            case Less:
                return data.compareTo(threshold) < 0;
            case Eq:
                return data.compareTo(threshold) == 0;
            default:
                return false;
        }
    }

    private boolean handleEventInRedis(SensorConfig.EventCheckLogic eventCheckLogic, Map<String, Object> event) {
        Set lastEventRecord;
        String redisKey = this.redisKeyUtils.getEventRedisKey(String.valueOf(event.get("SensorId"))) + ":" + eventCheckLogic.getEventType();
        lastEventRecord = this.redisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0L, 0L);
            if (this.redisTemplate.hasKey(redisKey).booleanValue() && !lastEventRecord.isEmpty()) {
            ZSetOperations.TypedTuple latest = (ZSetOperations.TypedTuple)lastEventRecord.iterator().next();
            String eventJSON = (String)latest.getValue();
            long eventTimeStamp = Objects.requireNonNull(latest.getScore()).longValue();
            if ((Long)event.get("TimeStamp") - eventTimeStamp <= this.sensorConfig.getEventDetectInterval()) {
                return false;
            }
        }
        HashMap<String, Map<String, Object>> eventToPublish = new HashMap<String, Map<String, Object>>();
        eventToPublish.put(eventCheckLogic.getEventType(), event);
        String eventToPublishJson = JsonUtils.toJson(eventToPublish);
        String event_stream_key = "real_time_event_stream";
        this.redisTemplate.convertAndSend(event_stream_key, eventToPublishJson);
        Long ts = (Long)event.get("TimeStamp");
        this.redisTemplate.opsForZSet().add(redisKey, JsonUtils.toJson(event), ts.doubleValue());
        long oneDayTimeSpan = System.currentTimeMillis() - 86400000L;
        this.redisUtils.removeFromZSetByScore(redisKey, 0.0, ((double)oneDayTimeSpan));
        return true;
    }

    private void handleDataInRedis(Map<String, Object> measurements) {
        if (!measurements.isEmpty()) {
            String key = this.redisKeyUtils.getMeasurementRedisKey(String.valueOf(measurements.get("SensorId")));
            log.debug("redis data key:{}", key);
            Long ts = (Long)measurements.get("TimeStamp");
            this.redisUtils.addToZSet(key, JsonUtils.toJson(measurements), ts.doubleValue());
            long cur = System.currentTimeMillis();
            log.debug("timestamp vs currmill: {},{}", ts, cur);
            long oneDayTimeSpan = cur - 86400000L;
            this.redisUtils.removeFromZSetByScore(key, 0.0, ((double)oneDayTimeSpan));
        }
    }

    private void storeDataInMap(Map<String, Object> measurements, SensorConfig.SaveData dataConf, SensorNodeData node) {
        log.debug("storeDataInMap {},{}", node, dataConf);
        if (!measurements.containsKey("RecordTime")) {
            Date recordTime = node.getRecordTime();
            measurements.put("RecordTime", recordTime.toString());
            log.debug("record time: {}", recordTime.toString());
            measurements.put("TimeStamp", recordTime.getTime());
        }
        String key = dataConf.getKey();
        Object value = node.getData(dataConf.getDataType(), (float)dataConf.getCoefficient());
        log.debug("get value for {}, value: {}", key, value);
        measurements.put(key, value);
    }
}

