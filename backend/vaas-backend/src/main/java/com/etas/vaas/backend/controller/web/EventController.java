/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.controller.web.EventController
 *  com.etas.vaas.backend.dto.request.DeleteEventRequest
 *  com.etas.vaas.backend.dto.request.EventRequest
 *  com.etas.vaas.backend.dto.request.SimulatedEvent
 *  com.etas.vaas.backend.dto.response.AlarmResponse
 *  com.etas.vaas.backend.dto.response.EventResponse
 *  com.etas.vaas.backend.enumeration.RoadEventType
 *  com.etas.vaas.backend.service.web.EventService
 *  com.etas.vaas.common.dto.response.ExternalEventDto
 *  com.etas.vaas.common.dto.response.VaaSResponseDto
 *  com.etas.vaas.common.service.ExternalEventService
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.format.annotation.DateTimeFormat
 *  org.springframework.format.annotation.DateTimeFormat$ISO
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.backend.controller.web;

import com.etas.vaas.backend.dto.request.DeleteEventRequest;
import com.etas.vaas.backend.dto.request.EventCountByVehicleRequest;
import com.etas.vaas.backend.dto.request.EventRequest;
import com.etas.vaas.backend.dto.request.SimulatedEvent;
import com.etas.vaas.backend.dto.response.AlarmResponse;
import com.etas.vaas.backend.dto.response.EventResponse;
import com.etas.vaas.backend.dto.response.VehicleEventCountResponse;
import com.etas.vaas.backend.enumeration.RoadEventType;
import com.etas.vaas.backend.service.web.EventService;
import com.etas.vaas.backend.service.web.VehicleStatService;
import jakarta.annotation.Resource;
import com.etas.vaas.common.dto.response.ExternalEventDto;
import com.etas.vaas.common.dto.response.VaaSResponseDto;
import com.etas.vaas.common.service.ExternalEventService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final ExternalEventService externalEventService;
    @Resource
    private VehicleStatService vehicleStatService;

    @PostMapping(value={"/get-alarm-list"})
    public ResponseEntity<List<AlarmResponse>> getSensorEvent(@RequestBody EventRequest eventRequest) {
        int hour = eventRequest.getHour();
        return ResponseEntity.ok(this.eventService.getAlarmList(hour));
    }

    @PostMapping(value={"/get-event-summary"})
    public ResponseEntity<String> getEventSummary() {
        return ResponseEntity.ok(this.eventService.getEventSummary());
    }

    /** 新增（非原版还原产物）：每辆采集车当天的颠簸/湿滑点位数量统计，供大屏排行展示 */
    @PostMapping(value={"/get-event-count-by-vehicle"})
    public ResponseEntity<List<VehicleEventCountResponse>> getEventCountByVehicle(@RequestBody EventCountByVehicleRequest request) {
        return ResponseEntity.ok(this.vehicleStatService.getEventCountByVehicle(request.getDate()));
    }

    @PostMapping(value={"/get-last-24h-bump-event"})
    public ResponseEntity<List<EventResponse>> getBumpyEventIn24(@RequestBody EventRequest eventRequest) {
        int hour = eventRequest.getHour();
        List bumpyEvents = this.eventService.getEventsInTimeRange(RoadEventType.BUMP.getValue(), hour, true);
        return ResponseEntity.ok(bumpyEvents);
    }

    @PostMapping(value={"/get-last-24h-slip-event"})
    public ResponseEntity<List<EventResponse>> getSlipperyEventIn24(@RequestBody EventRequest eventRequest) {
        int hour = eventRequest.getHour();
        List slipperyEvents = this.eventService.getEventsInTimeRange(RoadEventType.SLIP.getValue(), hour, false);
        return ResponseEntity.ok(slipperyEvents);
    }

    @PostMapping(value={"/get-last-24h-ice-event"})
    public ResponseEntity<List<EventResponse>> getIceEventIn24(@RequestBody EventRequest eventRequest) {
        return ResponseEntity.ok(new ArrayList());
    }

    @PostMapping(value={"/get-last-24h-ponding-event"})
    public ResponseEntity<List<EventResponse>> getWaterEventIn24(@RequestBody EventRequest eventRequest) {
        int hour = eventRequest.getHour();
        List pongdingEvents = this.eventService.getEventsInTimeRange(RoadEventType.PONDING.getValue(), hour, false);
        return ResponseEntity.ok(pongdingEvents);
    }

    @PostMapping(value={"/get-last-24h-low-attachment-event"})
    public ResponseEntity<List<EventResponse>> getLowAttachmentEventIn24(@RequestBody EventRequest eventRequest) {
        return ResponseEntity.ok(new ArrayList());
    }

    @PostMapping(value={"/delete-event"})
    public ResponseEntity<String> deleteEvent(@RequestBody DeleteEventRequest deleteEventRequest) {
        log.info("deleteEventRequest eventId: {}", deleteEventRequest.getEventId());
        log.info("deleteEventRequest eventType: {}", deleteEventRequest.getEventType());
        return ResponseEntity.ok(this.eventService.deleteEvent(deleteEventRequest));
    }

    @GetMapping(value={"/external/getEventSummary/{startTime}/{endTime}"})
    public VaaSResponseDto<List<ExternalEventDto>> getEventSummary(@PathVariable @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime, @PathVariable @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("enter getEventSummary");
        return this.externalEventService.getEventSummaryForExternal(startTime, endTime);
    }

    @GetMapping(value={"delete-all-events"})
    public VaaSResponseDto<Void> deleteEvent() {
        return this.eventService.deleteAllEvents();
    }

    @PostMapping(value={"post-simulated-event"})
    public VaaSResponseDto<Void> postSimulatedEvent(@RequestBody SimulatedEvent simulatedEvent) {
        return this.eventService.saveSimulatedEvent(simulatedEvent);
    }

    public EventController(EventService eventService, ExternalEventService externalEventService) {
        this.eventService = eventService;
        this.externalEventService = externalEventService;
    }
}

