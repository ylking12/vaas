/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.common.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.etas.vaas.common.dto.response.ExternalEventDto;
import com.etas.vaas.common.dto.response.VaaSResponseDto;
import com.etas.vaas.common.entity.Event;
import com.etas.vaas.common.enums.SourceType;
import com.etas.vaas.common.mapper.EventMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExternalEventService {
    private static final Logger log = LoggerFactory.getLogger(ExternalEventService.class);
    @Resource
    private EventMapper eventMapper;

    public VaaSResponseDto<List<ExternalEventDto>> getEventSummaryForExternal(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            log.debug("start time: {}", startTime);
            log.debug("end time: {}", endTime);
            ArrayList<ExternalEventDto> outputEventList = new ArrayList<ExternalEventDto>();
            LambdaQueryWrapper<Event> queryWrapper = new LambdaQueryWrapper<Event>()
                .between(Event::getEventTime, startTime, endTime)
                .orderByAsc(Event::getEventTime);
            List<Event> events = this.eventMapper.selectList(queryWrapper);
            for (Event event : events) {
                ExternalEventDto outputEvent = new ExternalEventDto();
                outputEvent.setEventId(event.getEventId());
                outputEvent.setEventType(event.getEventType().getTypeString());
                outputEvent.setSourceId(event.getSourceId());
                outputEvent.setRoadName(event.getRoadName());
                outputEvent.setLongitude(event.getLongitude());
                outputEvent.setLatitude(event.getLatitude());
                outputEvent.setH3Hash(event.getH3Hash());
                outputEvent.setLevel(event.getLevel());
                if (event.getSourceType() == SourceType.KT || event.getSourceType() == SourceType.MOTION_SENSOR) {
                    outputEvent.setSourceType("vehicle");
                } else {
                    outputEvent.setSourceType("sensor");
                }
                LocalDateTime eventTimeWithoutNano = event.getEventTime().withNano(0);
                String formattedWithoutNanos = eventTimeWithoutNano.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                outputEvent.setEventTime(formattedWithoutNanos);
                outputEventList.add(outputEvent);
            }
            log.debug("output events\uff1a {}", outputEventList);
            return new VaaSResponseDto<List<ExternalEventDto>>(200, "successful retrieved data", outputEventList);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new VaaSResponseDto<>(-1, "Error retrieving event data: " + e.getMessage(), null);
        }
    }

    public ExternalEventService() {
    }
}

