/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.RoadEvent
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoadEvent {
    @JsonProperty(value="status")
    private Integer status;
    @JsonProperty(value="event_type")
    private String eventType;
    @JsonProperty(value="imei")
    private String imei;
    @JsonProperty(value="dongle_id")
    private String sn;
    @JsonProperty(value="car_type")
    private String carType;
    @JsonProperty(value="event_time")
    private String eventTime;
    @JsonProperty(value="event_timestamp")
    private long eventTimestamp;
    @JsonProperty(value="received_time")
    private String receivedTime;
    @JsonProperty(value="received_timestamp")
    private long receivedTimestamp;
    @JsonProperty(value="perception_time")
    private String perceptionTime;
    @JsonProperty(value="perception_timestamp")
    private long perceptionTimestamp;
    @JsonProperty(value="coordinates")
    private String coordinates;
    @JsonProperty(value="longitude")
    private double longitude;
    @JsonProperty(value="latitude")
    private double latitude;
    @JsonProperty(value="is_duplicate")
    private String isDuplicate;
    @JsonProperty(value="in_xidong")
    private String inArea;
    @JsonProperty(value="road_name")
    private String roadName;

    public RoadEvent() {
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getImei() {
        return this.imei;
    }

    public String getSn() {
        return this.sn;
    }

    public String getCarType() {
        return this.carType;
    }

    public String getEventTime() {
        return this.eventTime;
    }

    public long getEventTimestamp() {
        return this.eventTimestamp;
    }

    public String getReceivedTime() {
        return this.receivedTime;
    }

    public long getReceivedTimestamp() {
        return this.receivedTimestamp;
    }

    public String getPerceptionTime() {
        return this.perceptionTime;
    }

    public long getPerceptionTimestamp() {
        return this.perceptionTimestamp;
    }

    public String getCoordinates() {
        return this.coordinates;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getIsDuplicate() {
        return this.isDuplicate;
    }

    public String getInArea() {
        return this.inArea;
    }

    public String getRoadName() {
        return this.roadName;
    }

    @JsonProperty(value="status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty(value="event_type")
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @JsonProperty(value="imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    @JsonProperty(value="dongle_id")
    public void setSn(String sn) {
        this.sn = sn;
    }

    @JsonProperty(value="car_type")
    public void setCarType(String carType) {
        this.carType = carType;
    }

    @JsonProperty(value="event_time")
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    @JsonProperty(value="event_timestamp")
    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    @JsonProperty(value="received_time")
    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    @JsonProperty(value="received_timestamp")
    public void setReceivedTimestamp(long receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    @JsonProperty(value="perception_time")
    public void setPerceptionTime(String perceptionTime) {
        this.perceptionTime = perceptionTime;
    }

    @JsonProperty(value="perception_timestamp")
    public void setPerceptionTimestamp(long perceptionTimestamp) {
        this.perceptionTimestamp = perceptionTimestamp;
    }

    @JsonProperty(value="coordinates")
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @JsonProperty(value="longitude")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty(value="latitude")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty(value="is_duplicate")
    public void setIsDuplicate(String isDuplicate) {
        this.isDuplicate = isDuplicate;
    }

    @JsonProperty(value="in_xidong")
    public void setInArea(String inArea) {
        this.inArea = inArea;
    }

    @JsonProperty(value="road_name")
    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RoadEvent)) {
            return false;
        }
        RoadEvent other = (RoadEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getEventTimestamp() != other.getEventTimestamp()) {
            return false;
        }
        if (this.getReceivedTimestamp() != other.getReceivedTimestamp()) {
            return false;
        }
        if (this.getPerceptionTimestamp() != other.getPerceptionTimestamp()) {
            return false;
        }
        if (Double.compare(this.getLongitude(), other.getLongitude()) != 0) {
            return false;
        }
        if (Double.compare(this.getLatitude(), other.getLatitude()) != 0) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        if (this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType)) {
            return false;
        }
        String this$imei = this.getImei();
        String other$imei = other.getImei();
        if (this$imei == null ? other$imei != null : !this$imei.equals(other$imei)) {
            return false;
        }
        String this$sn = this.getSn();
        String other$sn = other.getSn();
        if (this$sn == null ? other$sn != null : !this$sn.equals(other$sn)) {
            return false;
        }
        String this$carType = this.getCarType();
        String other$carType = other.getCarType();
        if (this$carType == null ? other$carType != null : !this$carType.equals(other$carType)) {
            return false;
        }
        String this$eventTime = this.getEventTime();
        String other$eventTime = other.getEventTime();
        if (this$eventTime == null ? other$eventTime != null : !this$eventTime.equals(other$eventTime)) {
            return false;
        }
        String this$receivedTime = this.getReceivedTime();
        String other$receivedTime = other.getReceivedTime();
        if (this$receivedTime == null ? other$receivedTime != null : !this$receivedTime.equals(other$receivedTime)) {
            return false;
        }
        String this$perceptionTime = this.getPerceptionTime();
        String other$perceptionTime = other.getPerceptionTime();
        if (this$perceptionTime == null ? other$perceptionTime != null : !this$perceptionTime.equals(other$perceptionTime)) {
            return false;
        }
        String this$coordinates = this.getCoordinates();
        String other$coordinates = other.getCoordinates();
        if (this$coordinates == null ? other$coordinates != null : !this$coordinates.equals(other$coordinates)) {
            return false;
        }
        String this$isDuplicate = this.getIsDuplicate();
        String other$isDuplicate = other.getIsDuplicate();
        if (this$isDuplicate == null ? other$isDuplicate != null : !this$isDuplicate.equals(other$isDuplicate)) {
            return false;
        }
        String this$inArea = this.getInArea();
        String other$inArea = other.getInArea();
        if (this$inArea == null ? other$inArea != null : !this$inArea.equals(other$inArea)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        return !(this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RoadEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $eventTimestamp = this.getEventTimestamp();
        result = result * 59 + (int)($eventTimestamp >>> 32 ^ $eventTimestamp);
        long $receivedTimestamp = this.getReceivedTimestamp();
        result = result * 59 + (int)($receivedTimestamp >>> 32 ^ $receivedTimestamp);
        long $perceptionTimestamp = this.getPerceptionTimestamp();
        result = result * 59 + (int)($perceptionTimestamp >>> 32 ^ $perceptionTimestamp);
        long $longitude = Double.doubleToLongBits(this.getLongitude());
        result = result * 59 + (int)($longitude >>> 32 ^ $longitude);
        long $latitude = Double.doubleToLongBits(this.getLatitude());
        result = result * 59 + (int)($latitude >>> 32 ^ $latitude);
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        String $imei = this.getImei();
        result = result * 59 + ($imei == null ? 43 : $imei.hashCode());
        String $sn = this.getSn();
        result = result * 59 + ($sn == null ? 43 : $sn.hashCode());
        String $carType = this.getCarType();
        result = result * 59 + ($carType == null ? 43 : $carType.hashCode());
        String $eventTime = this.getEventTime();
        result = result * 59 + ($eventTime == null ? 43 : $eventTime.hashCode());
        String $receivedTime = this.getReceivedTime();
        result = result * 59 + ($receivedTime == null ? 43 : $receivedTime.hashCode());
        String $perceptionTime = this.getPerceptionTime();
        result = result * 59 + ($perceptionTime == null ? 43 : $perceptionTime.hashCode());
        String $coordinates = this.getCoordinates();
        result = result * 59 + ($coordinates == null ? 43 : $coordinates.hashCode());
        String $isDuplicate = this.getIsDuplicate();
        result = result * 59 + ($isDuplicate == null ? 43 : $isDuplicate.hashCode());
        String $inArea = this.getInArea();
        result = result * 59 + ($inArea == null ? 43 : $inArea.hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        return result;
    }

    public String toString() {
        return "RoadEvent(status=" + this.getStatus() + ", eventType=" + this.getEventType() + ", imei=" + this.getImei() + ", sn=" + this.getSn() + ", carType=" + this.getCarType() + ", eventTime=" + this.getEventTime() + ", eventTimestamp=" + this.getEventTimestamp() + ", receivedTime=" + this.getReceivedTime() + ", receivedTimestamp=" + this.getReceivedTimestamp() + ", perceptionTime=" + this.getPerceptionTime() + ", perceptionTimestamp=" + this.getPerceptionTimestamp() + ", coordinates=" + this.getCoordinates() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", isDuplicate=" + this.getIsDuplicate() + ", inArea=" + this.getInArea() + ", roadName=" + this.getRoadName() + ")";
    }
}

