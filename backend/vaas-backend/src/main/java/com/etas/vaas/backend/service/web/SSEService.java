/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.service.web.SSEService
 *  com.etas.vaas.common.dao.VehicleEventDao
 *  com.etas.vaas.common.utils.JsonUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.web;

import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.utils.JsonUtils;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SSEService {
    private static final Logger log = LoggerFactory.getLogger(SSEService.class);

    public Map<String, Object> generateEventItem(String eventJson) {
        log.info("subscribed event Json: {}", eventJson);
        try {
            VehicleEventDao vehicleEvent = (VehicleEventDao)JsonUtils.parseJson((String)eventJson, VehicleEventDao.class);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("eventId", vehicleEvent.getEventId());
            map.put("eventType", vehicleEvent.getEventType());
            map.put("longitude", vehicleEvent.getLongitude());
            map.put("latitude", vehicleEvent.getLatitude());
            map.put("level", vehicleEvent.getLevel());
            log.info("published event: {}", map);
            return map;
        }
        catch (Exception e) {
            log.error("Failed to generate event item from json: {}", eventJson, e);
            throw e;
        }
    }

    public SSEService() {
    }
}

