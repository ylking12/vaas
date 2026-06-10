/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.etas.vaas.backend.dto.export.EventExcelTableDTO
 *  com.etas.vaas.backend.service.web.ExportService
 *  com.etas.vaas.common.component.FleetManagementComponent
 *  com.etas.vaas.common.entity.Event
 *  com.etas.vaas.common.entity.FleetManagement
 *  com.etas.vaas.common.mapper.EventMapper
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.web;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.etas.vaas.backend.dto.export.EventExcelTableDTO;
import com.etas.vaas.common.component.FleetManagementComponent;
import com.etas.vaas.common.entity.Event;
import com.etas.vaas.common.entity.FleetManagement;
import com.etas.vaas.common.mapper.EventMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExportService {
    private static final Logger log = LoggerFactory.getLogger(ExportService.class);
    @Resource
    private EventMapper eventMapper;
    @Resource
    private FleetManagementComponent fleetManagementComponent;

    public List<EventExcelTableDTO> getEventByTimeRange() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(24L);
        LambdaQueryWrapper<Event> queryWrapper = new LambdaQueryWrapper<Event>();
        queryWrapper.between(Event::getEventTime, startTime, endTime);
        queryWrapper.orderByAsc(Event::getEventTime);
        List<Event> events = this.eventMapper.selectList(queryWrapper);
        ArrayList<EventExcelTableDTO> outputEventList = new ArrayList<EventExcelTableDTO>();
        log.info("events size: {}", events.size());
        for (Event event : events) {
            EventExcelTableDTO eventExcelTableDTO = new EventExcelTableDTO();
            eventExcelTableDTO.setEventId(event.getEventId());
            eventExcelTableDTO.setEventType(event.getEventType().getChineseName());
            if (event.getSourceId() == null) {
                eventExcelTableDTO.setPlateNumber("\u8def\u6d4b\u6c14\u8c61\u7ad9");
            } else {
                String plateNumber = ((FleetManagement)this.fleetManagementComponent.getDeviceId2CarMap().get(event.getSourceId())).getPlate();
                if (plateNumber != null) {
                    String maskedPlateNumber = plateNumber.substring(0, plateNumber.length() - 2) + "**";
                    eventExcelTableDTO.setPlateNumber(maskedPlateNumber);
                } else {
                    eventExcelTableDTO.setPlateNumber("\u82cfB ****");
                }
            }
            eventExcelTableDTO.setEventTime(event.getEventTime());
            eventExcelTableDTO.setRoadName(event.getRoadName());
            eventExcelTableDTO.setLongitude(event.getLongitude());
            eventExcelTableDTO.setLatitude(event.getLatitude());
            outputEventList.add(eventExcelTableDTO);
        }
        return outputEventList;
    }
}

