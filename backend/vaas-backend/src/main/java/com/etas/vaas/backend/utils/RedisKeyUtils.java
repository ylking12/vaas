/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.configuration.SensorConfig
 *  com.etas.vaas.backend.utils.RedisKeyUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.backend.utils;

import com.etas.vaas.backend.configuration.SensorConfig;
import java.text.MessageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtils {
    @Autowired
    SensorConfig sensorConfig;

    public String getMeasurementRedisKey(String sensorId) {
        return MessageFormat.format((String)this.sensorConfig.getCache().getFormat().get("measurement").getKey(), sensorId);
    }

    public String getEventRedisKey(String sensorId) {
        return MessageFormat.format((String)this.sensorConfig.getCache().getFormat().get("event").getKey(), sensorId);
    }
}

