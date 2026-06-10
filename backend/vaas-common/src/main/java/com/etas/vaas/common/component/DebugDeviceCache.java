/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.common.component;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DebugDeviceCache {
    private static final Logger log = LoggerFactory.getLogger(DebugDeviceCache.class);
    private final Set<String> debugDevicesSet = new HashSet<String>();

    public void addToDebugSet(String deviceId) {
        this.debugDevicesSet.add(deviceId);
        log.info("add device {} to debug set", deviceId);
    }

    public void removeFromDebugSet(String deviceId) {
        this.debugDevicesSet.remove(deviceId);
        log.info("remove device {} from debug set", deviceId);
    }

    public boolean isDebugDevice(String deviceId) {
        return this.debugDevicesSet.contains(deviceId);
    }
}

