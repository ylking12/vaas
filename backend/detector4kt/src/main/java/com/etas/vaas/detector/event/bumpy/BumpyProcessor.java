/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.dto.KtVehicleEvent
 *  com.etas.vaas.common.enums.EventType
 *  com.etas.vaas.common.utils.MathUtils
 *  com.etas.vaas.common.utils.TimeUtils
 *  com.etas.vaas.detector.config.SensitivityConfig
 *  com.etas.vaas.detector.entity.Frame
 *  com.etas.vaas.detector.entity.IntermediateResult
 *  com.etas.vaas.detector.event.BaseProcessor
 *  com.etas.vaas.detector.event.EventInterface
 *  com.etas.vaas.detector.event.bumpy.BumpyProcessor
 *  com.etas.vaas.detector.event.bumpy.BumpyProcessor$Statistics
 *  jakarta.annotation.PostConstruct
 *  jakarta.annotation.Resource
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.annotation.Scope
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.detector.event.bumpy;

import com.etas.vaas.common.dto.KtVehicleEvent;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.utils.MathUtils;
import com.etas.vaas.common.utils.TimeUtils;
import com.etas.vaas.detector.config.SensitivityConfig;
import com.etas.vaas.detector.entity.Frame;
import com.etas.vaas.detector.entity.IntermediateResult;
import com.etas.vaas.detector.event.BaseProcessor;
import com.etas.vaas.detector.event.EventInterface;
import com.etas.vaas.detector.event.bumpy.BumpyProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value="prototype")
public class BumpyProcessor
extends BaseProcessor
implements EventInterface {
    private static final Logger log = LoggerFactory.getLogger(BumpyProcessor.class);
    private final Map<String, Float> zeroSpeedDuration = new HashMap();
    private final Map<String, List<Frame>> dataCache = new HashMap();
    @Value(value="${processor.bumpy.distance}")
    private float distance;
    @Value(value="${mode}")
    private String mode;
    @Value(value="${kt-timezone}")
    private String ktTimezone;
    @Resource
    private SensitivityConfig sensitivityConfig;
    private float sensLv1;
    private float sensLv2;

    @PostConstruct
    void init() {
        this.sensLv1 = (float) this.sensitivityConfig.getKt().getBump().getSteerRatioDiffLv1();
        this.sensLv2 = (float) this.sensitivityConfig.getKt().getBump().getSteerRatioDiffLv2();
    }

    public void updateDataCache(Frame frame, String serialNumber) {
        List cachedDataList = (List)this.dataCache.get(serialNumber);
        if (cachedDataList.isEmpty()) {
            if (frame.getVehicleSpd().equals(Float.valueOf(Float.MIN_VALUE))) {
                frame.setVehicleSpd(Float.valueOf(0.0f));
            }
            this.dataCache.put(serialNumber, new ArrayList());
            ((List)this.dataCache.get(serialNumber)).add(frame);
            return;
        }
        if (frame.getVehicleSpd().equals(Float.valueOf(Float.MIN_VALUE))) {
            frame.setVehicleSpd(((Frame)cachedDataList.get(cachedDataList.size() - 1)).getVehicleSpd());
        }
        LocalDateTime currentFrameTime = TimeUtils.strToObj((String)frame.getDate());
        Frame previousFrame = (Frame)cachedDataList.get(cachedDataList.size() - 1);
        LocalDateTime previousFrameTime = TimeUtils.strToObj((String)previousFrame.getDate());
        Float deltaTime = Float.valueOf((float)Duration.between(previousFrameTime, currentFrameTime).toMillis() / 1000.0f);
        frame.setDeltaTime(deltaTime);
        Float currentSpeed = frame.getVehicleSpd();
        Float previousSpeed = previousFrame.getVehicleSpd();
        Float averageSpeed = Float.valueOf((float)((double)(currentSpeed.floatValue() + previousSpeed.floatValue()) / 2.0));
        frame.setDistance(Float.valueOf(deltaTime.floatValue() * averageSpeed.floatValue()));
        ((List)this.dataCache.get(serialNumber)).add(frame);
        if (frame.getVehicleSpd().floatValue() < 1.0f) {
            if (this.zeroSpeedDuration.containsKey(serialNumber)) {
                Float cumulatedZeroSpeedDuration = Float.valueOf(((Float)this.zeroSpeedDuration.get(serialNumber)).floatValue() + frame.getDeltaTime().floatValue());
                this.zeroSpeedDuration.put(serialNumber, cumulatedZeroSpeedDuration);
            } else {
                this.zeroSpeedDuration.put(serialNumber, frame.getDeltaTime());
            }
            if (((Float)this.zeroSpeedDuration.get(serialNumber)).floatValue() > 1.0f) {
                this.dataCache.put(serialNumber, new ArrayList());
                this.zeroSpeedDuration.put(serialNumber, Float.valueOf(0.0f));
            }
        } else {
            this.zeroSpeedDuration.put(serialNumber, Float.valueOf(0.0f));
        }
    }

    public List<Frame> getSlidingWindow(String serialNumber) {
        ArrayList dataToObserve = new ArrayList();
        float cumulativeSumDistance = 0.0f;
        List dongleFrameList = (List)this.dataCache.get(serialNumber);
        for (int i = dongleFrameList.size() - 1; i > 0; --i) {
            if (!((cumulativeSumDistance += ((Frame)dongleFrameList.get(i)).getDistance().floatValue()) >= this.distance)) continue;
            dataToObserve = new ArrayList(dongleFrameList.subList(i - 1, dongleFrameList.size()));
            this.dataCache.put(serialNumber, dataToObserve);
            break;
        }
        return dataToObserve;
    }

    public List<Frame> processSignal(List<Frame> dataToObserve) {
        for (Frame frame : dataToObserve) {
            if (frame.getFlWheelSpd().equals(Float.valueOf(Float.MIN_VALUE)) || frame.getRlWheelSpd().equals(Float.valueOf(Float.MIN_VALUE)) || frame.getSteerWheelAngle().equals(Float.valueOf(Float.MIN_VALUE))) {
                frame.setLeftSpeedDiff(null);
                frame.setLeftSteeringRatio(null);
            } else {
                frame.setLeftSpeedDiff(Float.valueOf((frame.getFlWheelSpd().floatValue() - frame.getRlWheelSpd().floatValue()) * 3.6f));
                frame.setLeftSteeringRatio(Float.valueOf(frame.getLeftSpeedDiff().floatValue() / (Math.abs(frame.getSteerWheelAngle().floatValue()) + 1.0f)));
            }
            if (frame.getFrWheelSpd().equals(Float.valueOf(Float.MIN_VALUE)) || frame.getRrWheelSpd().equals(Float.valueOf(Float.MIN_VALUE)) || frame.getSteerWheelAngle().equals(Float.valueOf(Float.MIN_VALUE))) {
                frame.setRightSpeedDiff(null);
                frame.setRightSteeringRatio(null);
                continue;
            }
            frame.setRightSpeedDiff(Float.valueOf((frame.getFrWheelSpd().floatValue() - frame.getRrWheelSpd().floatValue()) * 3.6f));
            frame.setRightSteeringRatio(Float.valueOf(frame.getRightSpeedDiff().floatValue() / (Math.abs(frame.getSteerWheelAngle().floatValue()) + 1.0f)));
        }
        return dataToObserve;
    }

    public Statistics computeStatistics(List<Frame> dataToObserve) {
        Statistics statistics = new Statistics();
        String startTime = dataToObserve.get(0).getDate();
        statistics.setStartTime(startTime);
        String endTime = dataToObserve.get(dataToObserve.size() - 1).getDate();
        statistics.setEndTime(endTime);
        float speedDiffRatio1 = 0.0f;
        float speedDiffRatio2 = 0.0f;
        float speedDiff = 0.0f;
        ArrayList<Float> brakePressureList = new ArrayList<Float>();
        ArrayList<Float> leftSpeedDiffList = new ArrayList<Float>();
        ArrayList<Float> rightSpeedDiffList = new ArrayList<Float>();
        ArrayList<Float> speedList = new ArrayList<Float>();
        CopyOnWriteArrayList<Frame> snapshot = new CopyOnWriteArrayList<Frame>(dataToObserve);
        for (Frame frame : snapshot) {
            if (frame.getLeftSteeringRatio() != null && Math.abs(frame.getLeftSteeringRatio().floatValue()) > this.sensLv1) {
                speedDiffRatio1 += 1.0f;
            }
            if (frame.getRightSteeringRatio() != null && Math.abs(frame.getRightSteeringRatio().floatValue()) > this.sensLv1) {
                speedDiffRatio1 += 1.0f;
            }
            if (frame.getLeftSpeedDiff() != null && Math.abs(frame.getLeftSpeedDiff().floatValue()) > 1.0f) {
                speedDiff += 1.0f;
            }
            if (frame.getRightSpeedDiff() != null && Math.abs(frame.getRightSpeedDiff().floatValue()) > 1.0f) {
                speedDiff += 1.0f;
            }
            brakePressureList.add(frame.getEscMcylinderPressure());
            leftSpeedDiffList.add(frame.getLeftSpeedDiff());
            rightSpeedDiffList.add(frame.getRightSpeedDiff());
            speedList.add(frame.getVehicleSpd());
        }
        statistics.setSumSpeedDiff(Float.valueOf(speedDiff));
        statistics.setSumSpeedDiffRatioThreshold1(Float.valueOf(speedDiffRatio1));
        statistics.setSumSpeedDiffRatioThreshold2(Float.valueOf(speedDiffRatio2));
        statistics.setMeanBrakePressure(Float.valueOf(MathUtils.calculateMeanOfList(brakePressureList)));
        statistics.setMeanSpeed(Float.valueOf(MathUtils.calculateMeanOfList(speedList)));
        Float standardLeftSpeedVariation = MathUtils.standardDeviationWithNullableValue(leftSpeedDiffList);
        Float standardRightSpeedVariation = MathUtils.standardDeviationWithNullableValue(rightSpeedDiffList);
        if (standardLeftSpeedVariation != null && standardLeftSpeedVariation.floatValue() != 0.0f && standardRightSpeedVariation != null && standardRightSpeedVariation.floatValue() != 0.0f) {
            float correlation = MathUtils.pearsonCorrelation(leftSpeedDiffList, rightSpeedDiffList);
            statistics.setCorrelation(Float.valueOf(correlation));
        } else {
            statistics.setCorrelation(Float.valueOf(Float.MIN_VALUE));
        }
        return statistics;
    }

    public KtVehicleEvent identify(Frame frame) {
        KtVehicleEvent newEvent = new KtVehicleEvent();
        String serialNumber = frame.getSn();
        if (!this.dataCache.containsKey(serialNumber)) {
            this.dataCache.put(serialNumber, new ArrayList());
            ((List)this.dataCache.get(serialNumber)).add(frame);
            this.flag.put(serialNumber, 0);
            this.zeroSpeedDuration.put(serialNumber, Float.valueOf(0.0f));
            this.adjacentPoints.put(serialNumber, new ArrayList());
            log.debug("first data with serial number : {}", (Object)serialNumber);
        }
        ArrayList<KtVehicleEvent> redisEventList = new ArrayList<KtVehicleEvent>();
        this.updateDataCache(frame, serialNumber);
        List dataToObserve = this.getSlidingWindow(serialNumber);
        if (!dataToObserve.isEmpty()) {
            boolean meanSpeedCondition;
            Statistics statistics = this.computeStatistics(dataToObserve = this.processSignal(dataToObserve));
            if (statistics.getCorrelation() == null) {
                newEvent.setStatus(Integer.valueOf(0));
                return newEvent;
            }
            boolean sumSpeedDiffCondition = statistics.getSumSpeedDiff() > 1.0f;
            boolean getSumSpeedDiffRatioThreshold1Condition = statistics.getSumSpeedDiffRatioThreshold1() >= 3.0f;
            boolean meanBrakePressureCondition = statistics.getMeanBrakePressure() < 1.0f;
            boolean correlationCondition = statistics.getCorrelation() < 0.5f;
            boolean bl = meanSpeedCondition = statistics.getMeanSpeed().floatValue() < 9.7f;
            if (sumSpeedDiffCondition && getSumSpeedDiffRatioThreshold1Condition && meanBrakePressureCondition && correlationCondition && meanSpeedCondition) {
                IntermediateResult intermediateResult = new IntermediateResult();
                intermediateResult.setTimeStamp(System.currentTimeMillis());
                intermediateResult.setFrameTime(statistics.getEndTime());
                intermediateResult.setReceivedTime(frame.getReceivedTime());
                intermediateResult.setSt(statistics);
                this.flag.put(serialNumber, 1);
                ((ArrayList)this.adjacentPoints.get(serialNumber)).add(intermediateResult);
            } else {
                Integer previousStatusFlag = (Integer)this.flag.get(serialNumber);
                this.flag.put(serialNumber, previousStatusFlag - 1);
                if (((ArrayList)this.adjacentPoints.get(serialNumber)).isEmpty()) {
                    this.flag.put(serialNumber, 0);
                } else if ((Integer)this.flag.get(serialNumber) < 0) {
                    String firstDeviceTime = ((IntermediateResult)((ArrayList)this.adjacentPoints.get(serialNumber)).get(0)).getFrameTime();
                    String firstReceiveTime = ((IntermediateResult)((ArrayList)this.adjacentPoints.get(serialNumber)).get(0)).getReceivedTime();
                    this.perceptionTime = LocalDateTime.now();
                    newEvent.setStatus(Integer.valueOf(1));
                    newEvent.setEventType(EventType.BUMP);
                    newEvent.setSn(serialNumber);
                    if ("test".equals(this.mode)) {
                        newEvent.setEventTimestamp(Long.valueOf(System.currentTimeMillis() - 5000L));
                    } else {
                        newEvent.setEventTimestamp(Long.valueOf(TimeUtils.strToTimestamp((String)firstDeviceTime, (String)this.ktTimezone)));
                    }
                    newEvent.setPerceptionTimestamp(TimeUtils.objToTimestamp((LocalDateTime)this.perceptionTime));
                    newEvent.setReceivedTimestamp(Long.valueOf(TimeUtils.strToTimestamp((String)firstReceiveTime, (String)this.ktTimezone)));
                    this.adjacentPoints.put(serialNumber, new ArrayList());
                    this.flag.put(serialNumber, 0);
                    redisEventList.add(newEvent);
                } else {
                    newEvent.setStatus(Integer.valueOf(0));
                    return newEvent;
                }
            }
        }
        return this.triggerDebounce(redisEventList, serialNumber);
    }

    public static class Statistics {
        private String startTime;
        private String endTime;
        private Float sumSpeedDiff;
        private Float sumSpeedDiffRatioThreshold1;
        private Float sumSpeedDiffRatioThreshold2;
        private Float meanBrakePressure;
        private Float meanSpeed;
        private Float correlation;
        public String getStartTime() { return startTime; }
        public void setStartTime(String v) { this.startTime = v; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String v) { this.endTime = v; }
        public Float getSumSpeedDiff() { return sumSpeedDiff; }
        public void setSumSpeedDiff(Float v) { this.sumSpeedDiff = v; }
        public Float getSumSpeedDiffRatioThreshold1() { return sumSpeedDiffRatioThreshold1; }
        public void setSumSpeedDiffRatioThreshold1(Float v) { this.sumSpeedDiffRatioThreshold1 = v; }
        public Float getSumSpeedDiffRatioThreshold2() { return sumSpeedDiffRatioThreshold2; }
        public void setSumSpeedDiffRatioThreshold2(Float v) { this.sumSpeedDiffRatioThreshold2 = v; }
        public Float getMeanBrakePressure() { return meanBrakePressure; }
        public void setMeanBrakePressure(Float v) { this.meanBrakePressure = v; }
        public Float getMeanSpeed() { return meanSpeed; }
        public void setMeanSpeed(Float v) { this.meanSpeed = v; }
        public Float getCorrelation() { return correlation; }
        public void setCorrelation(Float v) { this.correlation = v; }
    }
}

