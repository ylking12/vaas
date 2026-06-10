/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.dto.KtVehicleEvent
 *  com.etas.vaas.detector.entity.IntermediateResult
 *  com.etas.vaas.detector.event.BaseProcessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.util.CollectionUtils
 */
package com.etas.vaas.detector.event;

import com.etas.vaas.common.dto.KtVehicleEvent;
import com.etas.vaas.detector.entity.IntermediateResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

public class BaseProcessor {
    private static final Logger log = LoggerFactory.getLogger(BaseProcessor.class);
    @Value(value="${processor.debounceInterval}")
    protected int debounceInterval;
    @Value(value="${processor.debounce}")
    protected boolean useDebounce;
    protected LocalDateTime perceptionTime;
    protected Map<String, Long> lastTimestamp = new HashMap();
    protected Map<String, Integer> flag = new HashMap();
    protected Map<String, ArrayList<IntermediateResult>> adjacentPoints = new HashMap();

    protected KtVehicleEvent triggerDebounce(List<KtVehicleEvent> resultList, String sn) {
        if (CollectionUtils.isEmpty(resultList)) {
            KtVehicleEvent newEvent = new KtVehicleEvent();
            newEvent.setStatus(Integer.valueOf(0));
            return newEvent;
        }
        if (!this.useDebounce) {
            return resultList.get(0);
        }
        if (!this.lastTimestamp.containsKey(sn)) {
            this.lastTimestamp.put(sn, resultList.get(0).getEventTimestamp());
            return resultList.get(0);
        }
        if (Math.abs(resultList.get(0).getEventTimestamp() - (Long)this.lastTimestamp.get(sn)) < (long)this.debounceInterval) {
            KtVehicleEvent newEvent = new KtVehicleEvent();
            newEvent.setStatus(Integer.valueOf(0));
            log.info("trigger debounce, sn: {}, lastTimestamp: {}, currentTimestamp: {}", new Object[]{sn, this.lastTimestamp.get(sn), resultList.get(0).getEventTimestamp()});
            return newEvent;
        }
        this.lastTimestamp.put(sn, resultList.get(0).getEventTimestamp());
        return resultList.get(0);
    }
}

