/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  jakarta.validation.constraints.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 *  org.springframework.validation.annotation.Validated
 */
package com.etas.vaas.common.service;

import com.etas.vaas.common.converter.EventConverter;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dao.WeatherEventDao;
import com.etas.vaas.common.entity.Event;
import com.etas.vaas.common.mapper.EventMapper;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class WriterService {
    private static final Logger log = LoggerFactory.getLogger(WriterService.class);
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private EventMapper eventMapper;

    @Transactional
    public int insertVehicleEvent(@NotNull VehicleEventDao eventDao) {
        Event event = EventConverter.INSTANCE.vehicleEventDao2Event(eventDao);
        return this.eventMapper.insert(event);
    }

    @Transactional
    public int insertWeatherEvent(@NotNull WeatherEventDao eventDao) {
        Event event = EventConverter.INSTANCE.weatherEventDao2Event(eventDao);
        return this.eventMapper.insert(event);
    }

    public <T> void addEvent2Redis(T content, String zsetKey) {
        String contentStr = JsonUtils.toStr(content);
        this.redisUtils.addToZSet(zsetKey, contentStr, System.currentTimeMillis());
    }

    public <T> void addEvent2Redis(T content, String zsetKey, long eventTimestamp) {
        String contentStr = JsonUtils.toStr(content);
        this.redisUtils.addToZSet(zsetKey, contentStr, eventTimestamp);
    }

    public WriterService() {
    }
}

