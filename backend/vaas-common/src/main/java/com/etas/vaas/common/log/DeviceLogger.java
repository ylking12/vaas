/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.helpers.MessageFormatter
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.common.log;

import com.etas.vaas.common.component.DebugDeviceCache;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

@Component
public class DeviceLogger {
    private static final Logger log = LoggerFactory.getLogger(DeviceLogger.class);
    @Resource
    private DebugDeviceCache debugDeviceCache;
    @Resource
    private RedisUtils redisUtils;

    public void info(String deviceId, String msg, Object ... args) {
        log.info(this.format(deviceId, msg, args));
    }

    public void debug(String deviceId, String msg, Object ... args) {
        if (this.debugDeviceCache.isDebugDevice(deviceId)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            String threadName = Thread.currentThread().getName();
            String logLevel = "DEBUG";
            String logString = this.format(deviceId, msg, args);
            String fullLog = String.format("%s [%s] %s - %s", timestamp, threadName, logLevel, logString);
            log.debug(logString);
            this.redisUtils.publishMessage("vaas:log", fullLog);
        }
    }

    private String format(String deviceId, String msg, Object ... args) {
        String formatted = MessageFormatter.arrayFormat((String)msg, (Object[])args).getMessage();
        return String.format("[%s] %s", deviceId, formatted);
    }
}

