/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.service.vehicle.RearMirrorService
 *  com.etas.vaas.backend.vo.RearMirrorResp
 *  com.etas.vaas.common.config.RedisKeyConfig
 *  com.etas.vaas.common.dao.VehicleEventDao
 *  com.etas.vaas.common.enums.EventType
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.etas.vaas.common.utils.RedisUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.data.redis.core.ZSetOperations$TypedTuple
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.vehicle;

import com.etas.vaas.backend.vo.RearMirrorResp;
import com.etas.vaas.common.config.RedisKeyConfig;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.utils.JsonUtils;
import com.etas.vaas.common.utils.RedisUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
public class RearMirrorService {
    private static final Logger log = LoggerFactory.getLogger(RearMirrorService.class);
    private final RedisUtils redisUtils;
    private final RedisKeyConfig redisKeyConfig;

    public List<RearMirrorResp> getEventForRearMirror(int minute) {
        VehicleEventDao event;
        long current = System.currentTimeMillis();
        long timeLeft = current - (long)minute * 60L * 1000L;
        Set<ZSetOperations.TypedTuple<String>> bumpyEventStrSet = this.redisUtils.getFromZSet(this.redisKeyConfig.getInstance().getBumpEventKey(), (double)timeLeft, (double)current);
        Set<ZSetOperations.TypedTuple<String>> slipperyEventStrSet = this.redisUtils.getFromZSet(this.redisKeyConfig.getInstance().getSlipEventKey(), (double)timeLeft, (double)current);
        log.debug("bumpyEventStrSet: {}", bumpyEventStrSet);
        log.debug("slipperyEventStrSet: {}", slipperyEventStrSet);
        ArrayList<VehicleEventDao> roadEventList = new ArrayList<VehicleEventDao>();
        for (ZSetOperations.TypedTuple eachStr : bumpyEventStrSet) {
            if (StringUtils.isBlank((CharSequence)((CharSequence)eachStr.getValue()))) {
                log.debug("bumpy event zset value is null, skip...");
                continue;
            }
            event = (VehicleEventDao)JsonUtils.parseJson((String)((String)eachStr.getValue()), VehicleEventDao.class);
            if (event == null) {
                log.debug("bumpy parse json get null object:{}", eachStr.getValue());
                continue;
            }
            roadEventList.add(event);
        }
        for (ZSetOperations.TypedTuple eachStr : slipperyEventStrSet) {
            if (StringUtils.isBlank((CharSequence)((CharSequence)eachStr.getValue()))) {
                log.debug("slippery event zset value is null, skip...");
                continue;
            }
            event = (VehicleEventDao)JsonUtils.parseJson((String)((String)eachStr.getValue()), VehicleEventDao.class);
            if (event == null) {
                log.debug("slippery parse json get null object:{}", eachStr.getValue());
                continue;
            }
            roadEventList.add(event);
        }
        log.debug("roadEventList: {}", roadEventList);
        ArrayList<RearMirrorResp> roadEventForRearMirrorList = new ArrayList<RearMirrorResp>();
        for (VehicleEventDao each : roadEventList) {
            RearMirrorResp eachForRearMirror = new RearMirrorResp();
            if (each.getEventType() == EventType.BUMP) {
                eachForRearMirror.setEventType("bumpy_event");
            } else {
                eachForRearMirror.setEventType("slippery_event");
            }
            eachForRearMirror.setLongitude(each.getLongitude().doubleValue());
            eachForRearMirror.setLatitude(each.getLatitude().doubleValue());
            roadEventForRearMirrorList.add(eachForRearMirror);
            log.debug("add road event to rear mirror resp list: {}", eachForRearMirror);
        }
        return roadEventForRearMirrorList;
    }

    public RearMirrorService(RedisUtils redisUtils, RedisKeyConfig redisKeyConfig) {
        this.redisUtils = redisUtils;
        this.redisKeyConfig = redisKeyConfig;
    }
}

