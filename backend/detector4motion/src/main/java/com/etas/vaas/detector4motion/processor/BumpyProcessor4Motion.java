/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.dao.VehicleEventDao
 *  com.etas.vaas.common.dto.MotionFrame
 *  com.etas.vaas.common.enums.EventType
 *  com.etas.vaas.common.enums.SourceType
 *  com.etas.vaas.detector4motion.dto.IntermediateResult4Motion
 *  com.etas.vaas.detector4motion.dto.Statistics
 *  com.etas.vaas.detector4motion.processor.BumpyProcessor4Motion
 *  jakarta.annotation.PostConstruct
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.annotation.Scope
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.detector4motion.processor;

import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dto.MotionFrame;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.enums.SourceType;
import com.etas.vaas.detector4motion.dto.IntermediateResult4Motion;
import com.etas.vaas.detector4motion.dto.Statistics;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value="prototype")
public class BumpyProcessor4Motion {
    private static final Logger log = LoggerFactory.getLogger(BumpyProcessor4Motion.class);
    @Value(value="${motion-processor.debounce.enabled:true}")
    private boolean debounceEnabled;
    @Value(value="${motion-processor.debounce.interval:2000}")
    private long debounceInterval;
    @Value(value="${motion-processor.thresholds.level7.zAmplitude:0.43}")
    private double zAmplitudeLevel7;
    @Value(value="${motion-processor.thresholds.level7.yAmplitude:0.25}")
    private double yAmplitudeLevel7;
    @Value(value="${motion-processor.thresholds.level5.zAmplitude:0.35}")
    private double zAmplitudeLevel5;
    @Value(value="${motion-processor.thresholds.level5.yAmplitude:0.25}")
    private double yAmplitudeLevel5;
    @Value(value="${motion-processor.thresholds.level3.zAmplitude:0.27}")
    private double zAmplitudeLevel3;
    @Value(value="${motion-processor.thresholds.level3.yAmplitude:0.23}")
    private double yAmplitudeLevel3;
    private final Map<String, Integer> zeroSpeedCount = new HashMap();
    private final Map<String, List<MotionFrame>> frameCache = new HashMap();
    private final Map<String, Integer> statusFlag = new HashMap();
    private final Map<String, List<IntermediateResult4Motion>> adjacentBumpPoint = new HashMap();
    private final Map<String, Long> deviceIdAndLastEventTimestamp = new HashMap();

    @PostConstruct
    public void init() {
        log.info("motion-processor.debounce.enabled:{}", (Object)this.debounceEnabled);
        log.info("motion-processor.debounce.interval:{}", (Object)this.debounceInterval);
        log.info("motion-processor.thresholds.level7.yAmplitude:{}", (Object)this.yAmplitudeLevel7);
        log.info("motion-processor.thresholds.level7.zAmplitude:{}", (Object)this.zAmplitudeLevel7);
        log.info("motion-processor.thresholds.level5.yAmplitude:{}", (Object)this.yAmplitudeLevel5);
        log.info("motion-processor.thresholds.level5.zAmplitude:{}", (Object)this.zAmplitudeLevel5);
        log.info("motion-processor.thresholds.level3.yAmplitude:{}", (Object)this.yAmplitudeLevel3);
        log.info("motion-processor.thresholds.level3.zAmplitude:{}", (Object)this.zAmplitudeLevel3);
    }

    private List<MotionFrame> updateDataCache(MotionFrame frame) {
        String deviceNumber = frame.getDeviceId();
        if (frame.getAZ() > 2.5) {
            return (List)this.frameCache.get(deviceNumber);
        }
        ((List)this.frameCache.get(deviceNumber)).add(frame);
        if (frame.getWX() == 0.0) {
            int count = (Integer)this.zeroSpeedCount.get(deviceNumber) + 1;
            if (count > 3) {
                this.frameCache.put(deviceNumber, new ArrayList());
                this.zeroSpeedCount.put(deviceNumber, 0);
            } else {
                this.zeroSpeedCount.put(deviceNumber, count);
            }
        } else {
            this.zeroSpeedCount.put(deviceNumber, 0);
        }
        List dataToObserve = (List)this.frameCache.get(deviceNumber);
        if (((List)this.frameCache.get(deviceNumber)).size() == 7) {
            List currentFrames = (List)this.frameCache.get(deviceNumber);
            this.frameCache.put(deviceNumber, currentFrames.subList(1, currentFrames.size()));
        }
        return dataToObserve;
    }

    private List<Double> computeAmplitude(List<Double> list) {
        log.trace("input list: {}", list);
        ArrayList<Double> filtered = new ArrayList<Double>();
        ArrayList<Integer> indexToDelete = new ArrayList<Integer>();
        for (int i = 0; i < list.size() - 1; ++i) {
            if (!list.get(i).equals(list.get(i + 1))) continue;
            indexToDelete.add(i);
        }
        for (int j = 0; j < list.size(); ++j) {
            if (indexToDelete.contains(j)) continue;
            filtered.add(list.get(j));
        }
        log.trace("filtered: {}", filtered);
        ArrayList<Integer> maxIndices = new ArrayList<Integer>();
        ArrayList<Integer> minIndices = new ArrayList<Integer>();
        for (int i = 1; i < filtered.size() - 1; ++i) {
            double prev = (Double)filtered.get(i - 1);
            double curr = (Double)filtered.get(i);
            double next = (Double)filtered.get(i + 1);
            if (curr > prev && curr > next) {
                maxIndices.add(i);
                continue;
            }
            if (!(curr < prev) || !(curr < next)) continue;
            minIndices.add(i);
        }
        ArrayList<Double> localMax = new ArrayList<Double>();
        Iterator prev = maxIndices.iterator();
        while (prev.hasNext()) {
            int idx = (Integer)prev.next();
            localMax.add((Double)filtered.get(idx));
        }
        ArrayList<Double> localMin = new ArrayList<Double>();
        Iterator idx = minIndices.iterator();
        while (idx.hasNext()) {
            int idx2 = (Integer)idx.next();
            localMin.add((Double)filtered.get(idx2));
        }
        ArrayList<Double> extrema = new ArrayList<Double>();
        int insertIndex;
        if (filtered.size() >= 2) {
            if ((Double)filtered.get(0) > (Double)filtered.get(1)) {
                extrema.addAll(localMax);
                for (int i = 0; i < localMin.size(); ++i) {
                    insertIndex = i * 2;
                    if (insertIndex <= extrema.size()) {
                        extrema.add(insertIndex, (Double)localMin.get(i));
                        continue;
                    }
                    extrema.add((Double)localMin.get(i));
                }
            } else {
                extrema.addAll(localMin);
                for (int i = 0; i < localMax.size(); ++i) {
                    insertIndex = i * 2;
                    if (insertIndex <= extrema.size()) {
                        extrema.add(insertIndex, (Double)localMax.get(i));
                        continue;
                    }
                    extrema.add((Double)localMax.get(i));
                }
            }
        }
        if (extrema.isEmpty()) {
            extrema.addAll(filtered);
        } else {
            if (!((Double)extrema.get(0)).equals(filtered.get(0))) {
                extrema.add(0, (Double)filtered.get(0));
            }
            if (!((Double)extrema.get(extrema.size() - 1)).equals(filtered.get(filtered.size() - 1))) {
                extrema.add((Double)filtered.get(filtered.size() - 1));
            }
        }
        ArrayList<Double> amplitudes = new ArrayList<Double>();
        for (int i = 1; i < extrema.size(); ++i) {
            amplitudes.add((Double)extrema.get(i) - (Double)extrema.get(i - 1));
        }
        return amplitudes;
    }

    public Statistics computeStatistics(List<MotionFrame> frames) {
        ArrayList<Double> ay = new ArrayList<Double>();
        ArrayList<Double> az = new ArrayList<Double>();
        for (MotionFrame frame : frames) {
            ay.add(frame.getAY());
            az.add(frame.getAZ());
        }
        List<Double> amplitudeAy = this.computeAmplitude(ay);
        List<Double> amplitudeAz = this.computeAmplitude(az);
        long countAy = amplitudeAy.stream().filter(v -> Math.abs((Double)v) > this.yAmplitudeLevel3).count();
        long countAz = amplitudeAz.stream().filter(v -> Math.abs((Double)v) > this.zAmplitudeLevel3).count();
        Statistics statistics = new Statistics();
        statistics.setStartTimestamp(frames.get(0).getTimestamp());
        statistics.setEndTimestamp(frames.get(frames.size() - 1).getTimestamp());
        statistics.setReceivedTimestamp(frames.get(frames.size() - 1).getReceivedTimestamp());
        statistics.setAmplitudeAy(Long.valueOf(countAy));
        statistics.setAmplitudeAz(Long.valueOf(countAz));
        Optional<Double> maxAbsAmplitudeAy = amplitudeAy.stream().max(Comparator.<Double>comparingDouble(Math::abs));
        if (maxAbsAmplitudeAy.isPresent()) {
            statistics.setMaxAmplitudeAy(Double.valueOf(Math.abs(maxAbsAmplitudeAy.get())));
        } else {
            statistics.setMaxAmplitudeAy(Double.valueOf(0.0));
        }
        Optional<Double> maxAbsAmplitudeAz = amplitudeAz.stream().max(Comparator.<Double>comparingDouble(Math::abs));
        if (maxAbsAmplitudeAz.isPresent()) {
            statistics.setMaxAmplitudeAz(Double.valueOf(Math.abs(maxAbsAmplitudeAz.get())));
        } else {
            statistics.setMaxAmplitudeAz(Double.valueOf(0.0));
        }
        return statistics;
    }

    public VehicleEventDao identify(MotionFrame frame) {
        String deviceId = frame.getDeviceId();
        if (!this.frameCache.containsKey(deviceId)) {
            ArrayList<MotionFrame> frames = new ArrayList<MotionFrame>();
            frames.add(frame);
            this.frameCache.put(deviceId, frames);
            this.statusFlag.put(deviceId, 0);
            this.zeroSpeedCount.put(deviceId, 0);
            this.adjacentBumpPoint.put(deviceId, new ArrayList());
            return null;
        }
        List framesToObserve = this.updateDataCache(frame);
        if (framesToObserve.size() >= 7) {
            boolean sumCondition;
            Statistics statistics = this.computeStatistics(framesToObserve);
            boolean amplitudeAy02Condition = statistics.getAmplitudeAy() >= 1L;
            boolean amplitudeAz025Condition = statistics.getAmplitudeAz() >= 1L;
            boolean bl = sumCondition = statistics.getAmplitudeAy() + statistics.getAmplitudeAz() >= 3L;
            if (amplitudeAy02Condition && amplitudeAz025Condition && sumCondition) {
                IntermediateResult4Motion intermediateResult = new IntermediateResult4Motion();
                intermediateResult.setStatus(Integer.valueOf(1));
                intermediateResult.setEndTimestamp(statistics.getEndTimestamp());
                intermediateResult.setReceivedTimestamp(statistics.getReceivedTimestamp());
                intermediateResult.setMaxAmplitudeAy(statistics.getMaxAmplitudeAy());
                intermediateResult.setMaxAmplitudeAz(statistics.getMaxAmplitudeAz());
                this.statusFlag.put(deviceId, 1);
                List previousIntermediateResults = (List)this.adjacentBumpPoint.get(deviceId);
                previousIntermediateResults.add(intermediateResult);
                this.adjacentBumpPoint.put(deviceId, previousIntermediateResults);
                this.frameCache.put(deviceId, new ArrayList());
            } else {
                this.statusFlag.put(deviceId, (Integer)this.statusFlag.get(deviceId) - 1);
                if (((List)this.adjacentBumpPoint.get(deviceId)).isEmpty()) {
                    this.statusFlag.put(deviceId, 0);
                    return null;
                }
                if ((Integer)this.statusFlag.get(deviceId) < 0) {
                    Long startTimestamp = ((IntermediateResult4Motion)((List)this.adjacentBumpPoint.get(deviceId)).get(0)).getEndTimestamp();
                    Long receivedTimestamp = ((IntermediateResult4Motion)((List)this.adjacentBumpPoint.get(deviceId)).get(0)).getReceivedTimestamp();
                    VehicleEventDao eventDto = new VehicleEventDao();
                    eventDto.setStatus(Integer.valueOf(1));
                    eventDto.setSourceType(SourceType.MOTION_SENSOR);
                    eventDto.setEventType(EventType.BUMP);
                    eventDto.setEventTimestamp(startTimestamp);
                    eventDto.setReceivedTimestamp(receivedTimestamp);
                    eventDto.setPerceptionTimestamp(Long.valueOf(System.currentTimeMillis()));
                    eventDto.setDeviceId(deviceId);
                    List previousIntermediateResults = (List)this.adjacentBumpPoint.get(deviceId);
                    double maxAmplitudeAy = ((IntermediateResult4Motion)previousIntermediateResults.get(previousIntermediateResults.size() - 1)).getMaxAmplitudeAy();
                    double maxAmplitudeAz = ((IntermediateResult4Motion)previousIntermediateResults.get(previousIntermediateResults.size() - 1)).getMaxAmplitudeAz();
                    log.info("max amplitudeAz: {}, max amplitudeAy: {} ", (Object)maxAmplitudeAz, (Object)maxAmplitudeAy);
                    if (maxAmplitudeAy > this.yAmplitudeLevel7 && maxAmplitudeAz > this.zAmplitudeLevel7) {
                        eventDto.setLevel(Integer.valueOf(7));
                    } else if (maxAmplitudeAy > this.yAmplitudeLevel5 && maxAmplitudeAz > this.zAmplitudeLevel5) {
                        eventDto.setLevel(Integer.valueOf(5));
                    } else {
                        eventDto.setLevel(Integer.valueOf(3));
                    }
                    this.adjacentBumpPoint.put(deviceId, new ArrayList());
                    this.statusFlag.put(deviceId, 0);
                    if (this.debounceEnabled) {
                        if (this.deviceIdAndLastEventTimestamp.containsKey(deviceId)) {
                            long deltaTimestamp = eventDto.getEventTimestamp() - (Long)this.deviceIdAndLastEventTimestamp.get(deviceId);
                            if (deltaTimestamp < this.debounceInterval) {
                                log.info("trigger debounce, time diff {}", (Object)deltaTimestamp);
                                return null;
                            }
                        } else {
                            this.deviceIdAndLastEventTimestamp.put(deviceId, eventDto.getEventTimestamp());
                            return eventDto;
                        }
                    }
                    return eventDto;
                }
            }
        }
        return null;
    }
}

