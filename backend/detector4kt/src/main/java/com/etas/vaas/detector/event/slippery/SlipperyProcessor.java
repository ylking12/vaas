/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.dto.KtVehicleEvent
 *  com.etas.vaas.common.enums.EventType
 *  com.etas.vaas.common.utils.TimeUtils
 *  com.etas.vaas.detector.entity.Frame
 *  com.etas.vaas.detector.entity.IntermediateResult
 *  com.etas.vaas.detector.event.BaseProcessor
 *  com.etas.vaas.detector.event.EventInterface
 *  com.etas.vaas.detector.event.slippery.SlipperyProcessor
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.annotation.Scope
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.detector.event.slippery;

import com.etas.vaas.common.dto.KtVehicleEvent;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.utils.TimeUtils;
import com.etas.vaas.detector.entity.Frame;
import com.etas.vaas.detector.entity.IntermediateResult;
import com.etas.vaas.detector.event.BaseProcessor;
import com.etas.vaas.detector.event.EventInterface;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/*
 * Exception performing whole class analysis ignored.
 */
@Component
@Scope(value="prototype")
public class SlipperyProcessor
extends BaseProcessor
implements EventInterface {
    private static final Logger log = LoggerFactory.getLogger(SlipperyProcessor.class);
    private final Map<String, Frame> lastFrame = new HashMap();
    @Value(value="${processor.slippery.speed-threshold}")
    private float speedThreshold;
    @Value(value="${processor.slippery.mu-threshold}")
    private float muThreshold;
    @Value(value="${mode}")
    private String mode;
    @Value(value="${kt-timezone}")
    private String ktTimezone;

    private static float calculateMu(Frame frame) {
        return (float)(Math.sqrt(frame.getLongitudeAcc().floatValue() * frame.getLongitudeAcc().floatValue() + frame.getLateralAcce().floatValue() * frame.getLateralAcce().floatValue()) / 9.8);
    }

    private boolean preCheck(Frame frame) {
        return frame.getDate() == null || frame.getSteerWheelAngle().floatValue() == Float.MIN_VALUE;
    }

    private float findMaxSlipRate(Frame frame) {
        float vehicleSpd = frame.getVehicleSpd().floatValue();
        if (vehicleSpd <= this.speedThreshold || Math.abs(frame.getSteerWheelAngle().floatValue()) > 50.0f) {
            return 0.0f;
        }
        float flSlipRate = (vehicleSpd - frame.getFlWheelSpd().floatValue()) / vehicleSpd;
        float frSlipRate = (vehicleSpd - frame.getFrWheelSpd().floatValue()) / vehicleSpd;
        float rlSlipRate = (vehicleSpd - frame.getRlWheelSpd().floatValue()) / vehicleSpd;
        float rrSlipRate = (vehicleSpd - frame.getRrWheelSpd().floatValue()) / vehicleSpd;
        return Math.max(Math.max(Math.abs(flSlipRate), Math.abs(frSlipRate)), Math.max(Math.abs(rlSlipRate), Math.abs(rrSlipRate)));
    }

    private float calculateMuMean(String sn) {
        float sum = 0.0f;
        int size = ((ArrayList)this.adjacentPoints.get(sn)).size();
        List<IntermediateResult> points = (List<IntermediateResult>) this.adjacentPoints.get(sn);
        for (IntermediateResult each : points) {
            sum += each.getMu();
        }
        return sum / (float)size;
    }

    private boolean fit(float slipRate, float cylinderPressure, float angelDiff) {
        return (double)slipRate > 0.2 && cylinderPressure > 5.0f && angelDiff < 100.0f;
    }

    private float calculateAngleDiff(Frame frame, String sn, float timeDiff) {
        return Math.abs(frame.getSteerWheelAngle().floatValue() - ((Frame)this.lastFrame.get(sn)).getSteerWheelAngle().floatValue()) / timeDiff;
    }

    public KtVehicleEvent identify(Frame frame) {
        String sn = frame.getSn();
        ArrayList<KtVehicleEvent> result = new ArrayList<KtVehicleEvent>();
        log.trace("kt time: {}", (Object)TimeUtils.strToTimestamp((String)frame.getDate(), (String)this.ktTimezone));
        boolean k1 = this.preCheck(frame);
        if (!this.lastFrame.containsKey(sn) || this.lastFrame.get(sn) == null) {
            if (!k1) {
                this.lastFrame.put(sn, frame);
                this.flag.put(sn, 0);
                this.adjacentPoints.put(sn, new ArrayList());
                log.debug("first data: {}", (Object)sn);
            }
        } else {
            float timeDiff = TimeUtils.countTimeDiff((String)frame.getDate(), (String)((Frame)this.lastFrame.get(sn)).getDate());
            float maxSlipRate = this.findMaxSlipRate(frame);
            float angelDiff = this.calculateAngleDiff(frame, sn, timeDiff);
            float mu = SlipperyProcessor.calculateMu((Frame)frame);
            this.lastFrame.put(sn, frame);
            if (this.fit(maxSlipRate, frame.getEscMcylinderPressure().floatValue(), angelDiff)) {
                IntermediateResult intermediateResult = new IntermediateResult();
                intermediateResult.setFrameTime(frame.getDate());
                intermediateResult.setReceivedTime(frame.getReceivedTime());
                intermediateResult.setMu(mu);
                ((ArrayList)this.adjacentPoints.get(sn)).add(intermediateResult);
                this.flag.put(sn, 1);
            } else {
                this.flag.put(sn, (Integer)this.flag.get(sn) - 1);
                if (((ArrayList)this.adjacentPoints.get(sn)).isEmpty()) {
                    this.flag.put(sn, 0);
                } else if ((Integer)this.flag.get(sn) < 0) {
                    float meanMu = this.calculateMuMean(sn);
                    if (meanMu < this.muThreshold) {
                        KtVehicleEvent newEvent = new KtVehicleEvent();
                        this.perceptionTime = LocalDateTime.now();
                        newEvent.setStatus(Integer.valueOf(1));
                        newEvent.setSn(sn);
                        newEvent.setEventType(EventType.SLIP);
                        if ("test".equals(this.mode)) {
                            newEvent.setEventTimestamp(Long.valueOf(System.currentTimeMillis() - 5000L));
                        } else {
                            newEvent.setEventTimestamp(Long.valueOf(TimeUtils.strToTimestamp((String)((IntermediateResult)((ArrayList)this.adjacentPoints.get(sn)).get(0)).getFrameTime(), (String)this.ktTimezone)));
                        }
                        newEvent.setPerceptionTimestamp(TimeUtils.objToTimestamp((LocalDateTime)this.perceptionTime));
                        newEvent.setReceivedTimestamp(Long.valueOf(TimeUtils.strToTimestamp((String)frame.getReceivedTime(), (String)this.ktTimezone)));
                        this.adjacentPoints.put(sn, new ArrayList());
                        this.flag.put(sn, 0);
                        result.add(newEvent);
                    } else {
                        this.adjacentPoints.put(sn, new ArrayList());
                        this.flag.put(sn, 0);
                    }
                }
            }
        }
        return this.triggerDebounce(result, sn);
    }
}

