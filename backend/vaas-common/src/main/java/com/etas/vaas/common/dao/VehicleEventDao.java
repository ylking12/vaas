/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dao;

import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.enums.SourceType;

public class VehicleEventDao {
    private String eventId;
    private EventType eventType;
    private String deviceId;
    private SourceType sourceType;
    private String roadName;
    private Double longitude;
    private Double latitude;
    private Boolean inArea;
    private Long eventTimestamp;
    private Long receivedTimestamp;
    private Long perceptionTimestamp;
    private Boolean duplicated;
    private Integer level;
    private Integer status;
    private String cellAddress;
    private Boolean simulated;

    public VehicleEventDao() {
    }

    public String getEventId() {
        return this.eventId;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public SourceType getSourceType() {
        return this.sourceType;
    }

    public String getRoadName() {
        return this.roadName;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Boolean getInArea() {
        return this.inArea;
    }

    public Long getEventTimestamp() {
        return this.eventTimestamp;
    }

    public Long getReceivedTimestamp() {
        return this.receivedTimestamp;
    }

    public Long getPerceptionTimestamp() {
        return this.perceptionTimestamp;
    }

    public Boolean getDuplicated() {
        return this.duplicated;
    }

    public Integer getLevel() {
        return this.level;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getCellAddress() {
        return this.cellAddress;
    }

    public Boolean getSimulated() {
        return this.simulated;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setInArea(Boolean inArea) {
        this.inArea = inArea;
    }

    public void setEventTimestamp(Long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public void setReceivedTimestamp(Long receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public void setPerceptionTimestamp(Long perceptionTimestamp) {
        this.perceptionTimestamp = perceptionTimestamp;
    }

    public void setDuplicated(Boolean duplicated) {
        this.duplicated = duplicated;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCellAddress(String cellAddress) {
        this.cellAddress = cellAddress;
    }

    public void setSimulated(Boolean simulated) {
        this.simulated = simulated;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof VehicleEventDao)) {
            return false;
        }
        VehicleEventDao other = (VehicleEventDao)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Double this$longitude = this.getLongitude();
        Double other$longitude = other.getLongitude();
        if (this$longitude == null ? other$longitude != null : !((Object)this$longitude).equals(other$longitude)) {
            return false;
        }
        Double this$latitude = this.getLatitude();
        Double other$latitude = other.getLatitude();
        if (this$latitude == null ? other$latitude != null : !((Object)this$latitude).equals(other$latitude)) {
            return false;
        }
        Boolean this$inArea = this.getInArea();
        Boolean other$inArea = other.getInArea();
        if (this$inArea == null ? other$inArea != null : !((Object)this$inArea).equals(other$inArea)) {
            return false;
        }
        Long this$eventTimestamp = this.getEventTimestamp();
        Long other$eventTimestamp = other.getEventTimestamp();
        if (this$eventTimestamp == null ? other$eventTimestamp != null : !((Object)this$eventTimestamp).equals(other$eventTimestamp)) {
            return false;
        }
        Long this$receivedTimestamp = this.getReceivedTimestamp();
        Long other$receivedTimestamp = other.getReceivedTimestamp();
        if (this$receivedTimestamp == null ? other$receivedTimestamp != null : !((Object)this$receivedTimestamp).equals(other$receivedTimestamp)) {
            return false;
        }
        Long this$perceptionTimestamp = this.getPerceptionTimestamp();
        Long other$perceptionTimestamp = other.getPerceptionTimestamp();
        if (this$perceptionTimestamp == null ? other$perceptionTimestamp != null : !((Object)this$perceptionTimestamp).equals(other$perceptionTimestamp)) {
            return false;
        }
        Boolean this$duplicated = this.getDuplicated();
        Boolean other$duplicated = other.getDuplicated();
        if (this$duplicated == null ? other$duplicated != null : !((Object)this$duplicated).equals(other$duplicated)) {
            return false;
        }
        Integer this$level = this.getLevel();
        Integer other$level = other.getLevel();
        if (this$level == null ? other$level != null : !((Object)this$level).equals(other$level)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        Boolean this$simulated = this.getSimulated();
        Boolean other$simulated = other.getSimulated();
        if (this$simulated == null ? other$simulated != null : !((Object)this$simulated).equals(other$simulated)) {
            return false;
        }
        String this$eventId = this.getEventId();
        String other$eventId = other.getEventId();
        if (this$eventId == null ? other$eventId != null : !this$eventId.equals(other$eventId)) {
            return false;
        }
        EventType this$eventType = this.getEventType();
        EventType other$eventType = other.getEventType();
        if (this$eventType == null ? other$eventType != null : !((Object)((Object)this$eventType)).equals((Object)other$eventType)) {
            return false;
        }
        String this$deviceId = this.getDeviceId();
        String other$deviceId = other.getDeviceId();
        if (this$deviceId == null ? other$deviceId != null : !this$deviceId.equals(other$deviceId)) {
            return false;
        }
        SourceType this$sourceType = this.getSourceType();
        SourceType other$sourceType = other.getSourceType();
        if (this$sourceType == null ? other$sourceType != null : !((Object)((Object)this$sourceType)).equals((Object)other$sourceType)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        if (this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName)) {
            return false;
        }
        String this$cellAddress = this.getCellAddress();
        String other$cellAddress = other.getCellAddress();
        return !(this$cellAddress == null ? other$cellAddress != null : !this$cellAddress.equals(other$cellAddress));
    }

    protected boolean canEqual(Object other) {
        return other instanceof VehicleEventDao;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Boolean $inArea = this.getInArea();
        result = result * 59 + ($inArea == null ? 43 : ((Object)$inArea).hashCode());
        Long $eventTimestamp = this.getEventTimestamp();
        result = result * 59 + ($eventTimestamp == null ? 43 : ((Object)$eventTimestamp).hashCode());
        Long $receivedTimestamp = this.getReceivedTimestamp();
        result = result * 59 + ($receivedTimestamp == null ? 43 : ((Object)$receivedTimestamp).hashCode());
        Long $perceptionTimestamp = this.getPerceptionTimestamp();
        result = result * 59 + ($perceptionTimestamp == null ? 43 : ((Object)$perceptionTimestamp).hashCode());
        Boolean $duplicated = this.getDuplicated();
        result = result * 59 + ($duplicated == null ? 43 : ((Object)$duplicated).hashCode());
        Integer $level = this.getLevel();
        result = result * 59 + ($level == null ? 43 : ((Object)$level).hashCode());
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        Boolean $simulated = this.getSimulated();
        result = result * 59 + ($simulated == null ? 43 : ((Object)$simulated).hashCode());
        String $eventId = this.getEventId();
        result = result * 59 + ($eventId == null ? 43 : $eventId.hashCode());
        EventType $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : ((Object)((Object)$eventType)).hashCode());
        String $deviceId = this.getDeviceId();
        result = result * 59 + ($deviceId == null ? 43 : $deviceId.hashCode());
        SourceType $sourceType = this.getSourceType();
        result = result * 59 + ($sourceType == null ? 43 : ((Object)((Object)$sourceType)).hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        String $cellAddress = this.getCellAddress();
        result = result * 59 + ($cellAddress == null ? 43 : $cellAddress.hashCode());
        return result;
    }

    public String toString() {
        return "VehicleEventDao(eventId=" + this.getEventId() + ", eventType=" + this.getEventType() + ", deviceId=" + this.getDeviceId() + ", sourceType=" + this.getSourceType() + ", roadName=" + this.getRoadName() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", inArea=" + this.getInArea() + ", eventTimestamp=" + this.getEventTimestamp() + ", receivedTimestamp=" + this.getReceivedTimestamp() + ", perceptionTimestamp=" + this.getPerceptionTimestamp() + ", duplicated=" + this.getDuplicated() + ", level=" + this.getLevel() + ", status=" + this.getStatus() + ", cellAddress=" + this.getCellAddress() + ", simulated=" + this.getSimulated() + ")";
    }
}

