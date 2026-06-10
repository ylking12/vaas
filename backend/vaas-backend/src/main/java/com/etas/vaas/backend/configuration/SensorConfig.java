/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.configuration.SensorConfig
 *  com.etas.vaas.backend.configuration.SensorConfig$CacheInfo
 *  com.etas.vaas.backend.configuration.SensorConfig$EventCheckLogic
 *  com.etas.vaas.backend.configuration.SensorConfig$SaveData
 *  com.etas.vaas.backend.configuration.SensorConfig$SdkInfo
 *  com.etas.vaas.backend.configuration.SensorConfig$SensorInfo
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.backend.configuration;

import com.etas.vaas.backend.configuration.SensorConfig;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix="sensors")
@Component
public class SensorConfig {
    private boolean enabled;
    private long eventDetectInterval;
    private Map<String, SdkInfo> sdks;
    private Map<String, SensorInfo> sensorIds;
    private CacheInfo cache;
    private Map<String, Map<String, SaveData>> data;
    private Map<String, EventCheckLogic> event;
    private Map<String, Map<String, String>> roadIds;
    private Map<String, String> dataMatchSensor;

    public SensorConfig() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public long getEventDetectInterval() {
        return this.eventDetectInterval;
    }

    public Map<String, SdkInfo> getSdks() {
        return this.sdks;
    }

    public Map<String, SensorInfo> getSensorIds() {
        return this.sensorIds;
    }

    public CacheInfo getCache() {
        return this.cache;
    }

    public Map<String, Map<String, SaveData>> getData() {
        return this.data;
    }

    public Map<String, EventCheckLogic> getEvent() {
        return this.event;
    }

    public Map<String, Map<String, String>> getRoadIds() {
        return this.roadIds;
    }

    public Map<String, String> getDataMatchSensor() {
        return this.dataMatchSensor;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEventDetectInterval(long eventDetectInterval) {
        this.eventDetectInterval = eventDetectInterval;
    }

    public void setSdks(Map<String, SdkInfo> sdks) {
        this.sdks = sdks;
    }

    public void setSensorIds(Map<String, SensorInfo> sensorIds) {
        this.sensorIds = sensorIds;
    }

    public void setCache(CacheInfo cache) {
        this.cache = cache;
    }

    public void setData(Map<String, Map<String, SaveData>> data) {
        this.data = data;
    }

    public void setEvent(Map<String, EventCheckLogic> event) {
        this.event = event;
    }

    public void setRoadIds(Map<String, Map<String, String>> roadIds) {
        this.roadIds = roadIds;
    }

    public void setDataMatchSensor(Map<String, String> dataMatchSensor) {
        this.dataMatchSensor = dataMatchSensor;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SensorConfig)) {
            return false;
        }
        SensorConfig other = (SensorConfig)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isEnabled() != other.isEnabled()) {
            return false;
        }
        if (this.getEventDetectInterval() != other.getEventDetectInterval()) {
            return false;
        }
        Map this$sdks = this.getSdks();
        Map other$sdks = other.getSdks();
        if (this$sdks == null ? other$sdks != null : !((Object)this$sdks).equals(other$sdks)) {
            return false;
        }
        Map this$sensorIds = this.getSensorIds();
        Map other$sensorIds = other.getSensorIds();
        if (this$sensorIds == null ? other$sensorIds != null : !((Object)this$sensorIds).equals(other$sensorIds)) {
            return false;
        }
        CacheInfo this$cache = this.getCache();
        CacheInfo other$cache = other.getCache();
        if (this$cache == null ? other$cache != null : !this$cache.equals(other$cache)) {
            return false;
        }
        Map this$data = this.getData();
        Map other$data = other.getData();
        if (this$data == null ? other$data != null : !((Object)this$data).equals(other$data)) {
            return false;
        }
        Map this$event = this.getEvent();
        Map other$event = other.getEvent();
        if (this$event == null ? other$event != null : !((Object)this$event).equals(other$event)) {
            return false;
        }
        Map this$roadIds = this.getRoadIds();
        Map other$roadIds = other.getRoadIds();
        if (this$roadIds == null ? other$roadIds != null : !((Object)this$roadIds).equals(other$roadIds)) {
            return false;
        }
        Map this$dataMatchSensor = this.getDataMatchSensor();
        Map other$dataMatchSensor = other.getDataMatchSensor();
        return !(this$dataMatchSensor == null ? other$dataMatchSensor != null : !((Object)this$dataMatchSensor).equals(other$dataMatchSensor));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SensorConfig;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isEnabled() ? 79 : 97);
        long $eventDetectInterval = this.getEventDetectInterval();
        result = result * 59 + (int)($eventDetectInterval >>> 32 ^ $eventDetectInterval);
        Map $sdks = this.getSdks();
        result = result * 59 + ($sdks == null ? 43 : ((Object)$sdks).hashCode());
        Map $sensorIds = this.getSensorIds();
        result = result * 59 + ($sensorIds == null ? 43 : ((Object)$sensorIds).hashCode());
        CacheInfo $cache = this.getCache();
        result = result * 59 + ($cache == null ? 43 : $cache.hashCode());
        Map $data = this.getData();
        result = result * 59 + ($data == null ? 43 : ((Object)$data).hashCode());
        Map $event = this.getEvent();
        result = result * 59 + ($event == null ? 43 : ((Object)$event).hashCode());
        Map $roadIds = this.getRoadIds();
        result = result * 59 + ($roadIds == null ? 43 : ((Object)$roadIds).hashCode());
        Map $dataMatchSensor = this.getDataMatchSensor();
        result = result * 59 + ($dataMatchSensor == null ? 43 : ((Object)$dataMatchSensor).hashCode());
        return result;
    }

    public String toString() {
        return "SensorConfig(enabled=" + this.isEnabled() + ", eventDetectInterval=" + this.getEventDetectInterval() + ", sdks=" + this.getSdks() + ", sensorIds=" + this.getSensorIds() + ", cache=" + this.getCache() + ", data=" + this.getData() + ", event=" + this.getEvent() + ", roadIds=" + this.getRoadIds() + ", dataMatchSensor=" + this.getDataMatchSensor() + ")";
    }


public enum DataType {
    Analog1, Analog2, Float, UNSigned32INT
}

public enum CompareType {
    GreatAndEq, LessAndEq, Great, Less
}

public enum SensorType {
    Station, RoadCondition, Atmospheric
}

public static class SdkInfo {
    private boolean enabled;
    private int port;
    private String host;
    private String className;
    private String paramFile;
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getParamFile() { return paramFile; }
    public void setParamFile(String paramFile) { this.paramFile = paramFile; }
}

public static class SensorInfo {
    private SensorType type;
    private String shortId;
    private String roadName;
    private String sensorName;
    private Map<String, Double> location;
    public SensorType getType() { return type; }
    public void setType(SensorType type) { this.type = type; }
    public String getShortId() { return shortId; }
    public void setShortId(String shortId) { this.shortId = shortId; }
    public String getRoadName() { return roadName; }
    public void setRoadName(String roadName) { this.roadName = roadName; }
    public String getSensorName() { return sensorName; }
    public void setSensorName(String sensorName) { this.sensorName = sensorName; }
    public Map<String, Double> getLocation() { return location; }
    public void setLocation(Map<String, Double> location) { this.location = location; }
}

public static class CacheInfo {
    private String measurement;
    private String event;
    private Map<String, FormatInfo> format;
    public String getMeasurement() { return measurement; }
    public void setMeasurement(String measurement) { this.measurement = measurement; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public Map<String, FormatInfo> getFormat() { return format; }
    public void setFormat(Map<String, FormatInfo> format) { this.format = format; }
    public static class FormatInfo {
        private DataType dataType;
        private int index;
        private String key;
        private String unit;
        private double coefficient;
        public DataType getDataType() { return dataType; }
        public void setDataType(DataType dataType) { this.dataType = dataType; }
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public double getCoefficient() { return coefficient; }
        public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
    }
}

public static class SaveData {
    private DataType dataType;
    private int index;
    private String key;
    private String unit;
    private double coefficient;
    public DataType getDataType() { return dataType; }
    public void setDataType(DataType dataType) { this.dataType = dataType; }
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getCoefficient() { return coefficient; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
}

public static class EventCheckLogic {
    private CompareType compareType;
    private DataType dataType;
    private String key;
    private String eventType;
    private String value;
    public CompareType getCompareType() { return compareType; }
    public void setCompareType(CompareType compareType) { this.compareType = compareType; }
    public DataType getDataType() { return dataType; }
    public void setDataType(DataType dataType) { this.dataType = dataType; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}

}

