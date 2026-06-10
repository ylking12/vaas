/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.TimeRange
 *  com.etas.vaas.backend.dto.request.DeleteEventRequest
 *  com.etas.vaas.backend.dto.request.SimulatedEvent
 *  com.etas.vaas.backend.dto.response.AlarmResponse
 *  com.etas.vaas.backend.dto.response.EventResponse
 *  com.etas.vaas.backend.enumeration.RoadEventType
 *  com.etas.vaas.backend.service.web.EventService
 *  com.etas.vaas.backend.service.web.EventService$1
 *  com.etas.vaas.backend.utils.TimeUtils
 *  com.etas.vaas.common.component.FleetManagementComponent
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.dao.VehicleEventDao
 *  com.etas.vaas.common.dto.response.VaaSResponseDto
 *  com.etas.vaas.common.entity.FleetManagement
 *  com.etas.vaas.common.enums.EventType
 *  com.etas.vaas.common.service.VehicleEventService
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  jakarta.annotation.Resource
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.web;

import com.etas.vaas.backend.dto.TimeRange;
import com.etas.vaas.backend.dto.request.DeleteEventRequest;
import com.etas.vaas.backend.dto.request.SimulatedEvent;
import com.etas.vaas.backend.dto.response.AlarmResponse;
import com.etas.vaas.backend.dto.response.EventResponse;
import com.etas.vaas.backend.enumeration.RoadEventType;
import com.etas.vaas.backend.service.web.EventService;
import com.etas.vaas.backend.utils.TimeUtils;
import com.etas.vaas.common.component.FleetManagementComponent;
import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dto.response.VaaSResponseDto;
import com.etas.vaas.common.entity.FleetManagement;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.service.VehicleEventService;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private final int bumpyMaintainThreshold = 1;
    private final int slipperyMaintainThreshold = 1;
    private final int waterMaintainThreshold = 1;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private RedisKeyConfig redisKeyConfig;
    @Resource
    private VehicleEventService vehicleEventService;
    @Resource
    private FleetManagementComponent fleetManagementComponent;
    @Value(value="${event.maxReturnedBumpEventAmount:375}")
    private Integer maxReturnedBumpEventAmount;

    public List<AlarmResponse> getAlarmList(int hour) {
        TimeRange timeRange = TimeUtils.getTimeRange((int)hour);
        log.info("getAlarmList left timeRange: {}, right timeRange: {}", timeRange.getLeft(), timeRange.getRight());
        log.info("getAlarmList left: {}, right: {}", TimeUtils.convertTsToDatetime((long)timeRange.getLeft()), TimeUtils.convertTsToDatetime((long)timeRange.getRight()));
        ArrayList<VehicleEventDao> cachedEvents = new ArrayList<VehicleEventDao>();
        List rawBumpEvent = this.getRawEventData(RoadEventType.BUMP.getValue(), timeRange.getLeft(), timeRange.getRight());
        for (Object each : rawBumpEvent) {
            VehicleEventDao cachedEvent = (VehicleEventDao)JsonUtils.parseJson((String)each, VehicleEventDao.class);
            cachedEvents.add(cachedEvent);
        }
        List rawSlipEvent = this.getRawEventData(RoadEventType.SLIP.getValue(), timeRange.getLeft(), timeRange.getRight());
        for (Object each : rawSlipEvent) {
            VehicleEventDao cachedEvent = (VehicleEventDao)JsonUtils.parseJson((String)each, VehicleEventDao.class);
            cachedEvents.add(cachedEvent);
        }
        List<String> rawPondingEvent = this.getRawEventData(RoadEventType.PONDING.getValue(), timeRange.getLeft(), timeRange.getRight());
        for (String each : rawPondingEvent) {
            VehicleEventDao cachedEvent = (VehicleEventDao)JsonUtils.parseJson((String)each, VehicleEventDao.class);
            cachedEvents.add(cachedEvent);
        }
        ArrayList<AlarmResponse> alarmList = new ArrayList<AlarmResponse>();
        for (VehicleEventDao cachedEvent : cachedEvents) {
            AlarmResponse alarmResponse = new AlarmResponse();
            alarmResponse.setRoadName(cachedEvent.getRoadName());
            alarmResponse.setDatetime(TimeUtils.timestampToLocalDateTime((long)cachedEvent.getEventTimestamp()));
            alarmResponse.setEventType(cachedEvent.getEventType().getChineseName());
            if (cachedEvent.getEventType() == EventType.PONDING) {
                alarmResponse.setSourceName("\u8def\u6d4b\u6c14\u8c61\u7ad9");
            } else {
                String plateNumber = ((FleetManagement)this.fleetManagementComponent.getDeviceId2CarMap().get(cachedEvent.getDeviceId())).getPlate();
                if (StringUtils.isEmpty((CharSequence)plateNumber)) {
                    alarmResponse.setSourceName("\u82cfB*****");
                } else {
                    String maskedPlateNumber = plateNumber.substring(0, plateNumber.length() - 3) + "***";
                    alarmResponse.setSourceName(maskedPlateNumber);
                }
            }
            alarmList.add(alarmResponse);
        }
        alarmList.sort(Comparator.comparing(AlarmResponse::getDatetime).reversed());
        if (alarmList.size() > this.maxReturnedBumpEventAmount) {
            return alarmList.stream().limit(this.maxReturnedBumpEventAmount.intValue()).collect(Collectors.toList());
        }
        return alarmList;
    }

    public List<EventResponse> getEventsInTimeRange(String eventType, int hour, boolean limitedAmount) {
        TimeRange timeRange = TimeUtils.getTimeRange((int)hour);
        log.info("getEventsInTimeRange left timeRange: {}, right timeRange: {}", timeRange.getLeft(), timeRange.getRight());
        log.info("getEventsInTimeRange left: {}, right: {}", TimeUtils.convertTsToDatetime((long)timeRange.getLeft()), TimeUtils.convertTsToDatetime((long)timeRange.getRight()));
        List<String> eventRawData = this.getRawEventData(eventType, timeRange.getLeft(), timeRange.getRight());
        ArrayList<EventResponse> eventResponses = new ArrayList<EventResponse>();
        for (String eventJson : eventRawData) {
            VehicleEventDao cachedEventDto = (VehicleEventDao)JsonUtils.parseJson((String)eventJson, VehicleEventDao.class);
            EventResponse eventResponse = new EventResponse();
            eventResponse.setEventType(cachedEventDto.getEventType());
            eventResponse.setLongitude(cachedEventDto.getLongitude());
            eventResponse.setLatitude(cachedEventDto.getLatitude());
            eventResponse.setEventId(cachedEventDto.getEventId());
            eventResponse.setLevel(cachedEventDto.getLevel());
            eventResponse.setEventTime(TimeUtils.timestampToLocalDateTime((long)cachedEventDto.getEventTimestamp()));
            eventResponses.add(eventResponse);
        }
        eventResponses.sort(Comparator.comparing(EventResponse::getEventTime).reversed());
        if (eventResponses.size() > this.maxReturnedBumpEventAmount) {
            return eventResponses.stream().limit(this.maxReturnedBumpEventAmount.intValue()).collect(Collectors.toList());
        }
        return eventResponses;
    }

    private List<String> getRawEventData(String eventType, long timeRangeLeft, long timeRangeRight) {
        log.debug("time range left : {}, time range right: {}", timeRangeLeft, timeRangeRight);
        log.debug("time left: {}, time right: {}", TimeUtils.convertTsToDatetime((long)timeRangeLeft), TimeUtils.convertTsToDatetime((long)timeRangeRight));
        ArrayList<String> rawData = new ArrayList<String>();
        switch (eventType) {
            case "bumpy_event": {
                rawData.addAll(this.fetchRedisData(this.redisKeyConfig.getInstance().getBumpEventKey(), timeRangeLeft, timeRangeRight));
                break;
            }
            case "low_attachment_event": {
                break;
            }
            case "ice_event": {
                break;
            }
            case "slippery_event": {
                rawData.addAll(this.fetchRedisData(this.redisKeyConfig.getInstance().getSlipEventKey(), timeRangeLeft, timeRangeRight));
                break;
            }
            case "ponding_event": {
                rawData.addAll(this.fetchRedisData(this.redisKeyConfig.getInstance().getPondingEventKey(), timeRangeLeft, timeRangeRight));
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown event type: " + eventType);
            }
        }
        return rawData;
    }

    private List<String> fetchRedisData(String key, long minScore, long maxScore) {
        Set<String> rawDataSet = this.redisUtils.getFromZSetWithScores(key, (double)minScore, (double)maxScore);
        return new ArrayList<String>(rawDataSet);
    }

    private List<String> getEventRoad(String eventType) {
        long timeRangeLeft = TimeUtils.getHourTimestamp((int)23);
        long timeRangeRight = TimeUtils.toEpochMilli((LocalDateTime)LocalDateTime.now());
        List<String> eventRawData = this.getRawEventData(eventType, timeRangeLeft, timeRangeRight);
        ArrayList<String> roadNames = new ArrayList<String>();
        for (String eventJson : eventRawData) {
            VehicleEventDao cachedEventDto = (VehicleEventDao)JsonUtils.parseJson((String)eventJson, VehicleEventDao.class);
            roadNames.add(cachedEventDto.getRoadName());
        }
        return roadNames;
    }

    private List<String> getRoadMaintain(String eventType) {
        int threshold = switch (eventType) {
            case "bumpy_event" -> 1;
            case "slippery_event" -> 1;
            case "water_event" -> 1;
            default -> 1;
        };
        List<String> roadList = this.getEventRoad(eventType);
        if (roadList.isEmpty()) {
            return Collections.emptyList();
        }
        HashMap<String, Integer> roadStats = new HashMap<String, Integer>();
        for (String road : roadList) {
            roadStats.put(road, roadStats.getOrDefault(road, 0) + 1);
        }
        ArrayList<String> roadToMaintainList = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : roadStats.entrySet()) {
            String road = (String)entry.getKey();
            int count = (Integer)entry.getValue();
            if (count < threshold) continue;
            roadToMaintainList.add(road);
        }
        return roadToMaintainList;
    }

    private int roadCount(List<String> roadList) {
        HashSet<String> roadSet = new HashSet<String>(roadList);
        return roadSet.size();
    }

    public String getEventSummary() {
        List bumpyRoad = this.getEventRoad("bumpy_event");
        List slipperyRoad = this.getEventRoad("slippery_event");
        List waterRoad = this.getEventRoad("ponding_event");
        List bumpyMaintain = this.getRoadMaintain("bumpy_event");
        List slipperyMaintain = this.getRoadMaintain("slippery_event");
        List waterMaintain = this.getRoadMaintain("ponding_event");
        HashMap<String, Object> eventSummary = new HashMap<String, Object>();
        eventSummary.put("water_road_amount", this.roadCount(waterRoad));
        eventSummary.put("slippery_road_amount", this.roadCount(slipperyRoad));
        eventSummary.put("bumpy_road_amount", this.roadCount(bumpyRoad));
        eventSummary.put("water_road_to_maintain", waterMaintain);
        eventSummary.put("slippery_road_to_maintain", slipperyMaintain);
        eventSummary.put("bumpy_road_to_maintain", bumpyMaintain);
        return JsonUtils.toJson(eventSummary);
    }

    public String deleteEvent(DeleteEventRequest request) {
        log.debug("delete event type\uff1a {}", request.getEventType());
        log.debug("delete event id\uff1a {}", request.getEventId());
        String redisKeyName = switch (request.getEventType()) {
            case BUMP -> this.redisKeyConfig.getInstance().getBumpEventKey();
            case SLIP -> this.redisKeyConfig.getInstance().getSlipEventKey();
            case PONDING -> this.redisKeyConfig.getInstance().getPondingEventKey();
            case LOW_FRICTION -> this.redisKeyConfig.getInstance().getLowAttachmentEventKey();
            case ICE -> this.redisKeyConfig.getInstance().getIceEventKey();
            default -> "";
        };
        Set<String> members = this.redisUtils.getAllFromZSet(redisKeyName);
        ArrayList<String> memberList = new ArrayList(members);
        for (String member : memberList) {
            VehicleEventDao event = (VehicleEventDao)JsonUtils.parseJson((String)member, VehicleEventDao.class);
            if (!Objects.equals(event.getEventId(), request.getEventId())) continue;
            log.info("deleting event: {}", event);
            Long redisResponse = this.redisUtils.removeFromZSetByMember(redisKeyName, member);
            log.info("delete event response from redis: {}", redisResponse);
            break;
        }
        return "ok";
    }

    public VaaSResponseDto<Void> deleteAllEvents() {
        this.redisUtils.deleteKey(this.redisKeyConfig.getInstance().getBumpEventKey());
        this.redisUtils.deleteKey(this.redisKeyConfig.getInstance().getSlipEventKey());
        this.redisUtils.deleteKey(this.redisKeyConfig.getInstance().getPondingEventKey());
        this.redisUtils.deleteKey(this.redisKeyConfig.getInstance().getLowAttachmentEventKey());
        this.redisUtils.deleteKey(this.redisKeyConfig.getInstance().getIceEventKey());
        return new VaaSResponseDto(200, "all 5 types events deleted", null);
    }

    public VaaSResponseDto<Void> saveSimulatedEvent(SimulatedEvent simulatedEvent) {
        String eventContent = JsonUtils.toStr(simulatedEvent);
        long now = System.currentTimeMillis();
        switch (simulatedEvent.getEventType()) {
            case BUMP: {
                this.redisUtils.addToZSet(this.redisKeyConfig.getInstance().getBumpEventKey(), eventContent, (double)now);
                break;
            }
            case SLIP: {
                this.redisUtils.addToZSet(this.redisKeyConfig.getInstance().getSlipEventKey(), eventContent, (double)now);
                break;
            }
            default: {
                throw new IllegalStateException("unknown event type!!!!");
            }
        }
        VehicleEventDao event = new VehicleEventDao();
        BeanUtils.copyProperties((Object)simulatedEvent, event);
        event.setSimulated(Boolean.valueOf(true));
        this.vehicleEventService.persistEvent(event);
        log.info("persisted simulated event: {}", event);
        return new VaaSResponseDto(200, "event cached", null);
    }
}

