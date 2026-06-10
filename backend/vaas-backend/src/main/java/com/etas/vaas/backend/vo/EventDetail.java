/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.vo.EventDetail
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.backend.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EventDetail {
    @JsonProperty(value="status")
    private Integer status;
    @JsonProperty(value="event_time")
    private String eventTime;
    @JsonProperty(value="coordinates")
    private List<Double> coordinates;
    @JsonProperty(value="event_type")
    private String eventType;
    @JsonProperty(value="dongle_id")
    private String sn;
    @JsonProperty(value="event_timestamp")
    private long eventTimestamp;
    @JsonProperty(value="longitude")
    private double longitude;
    @JsonProperty(value="latitude")
    private double latitude;
    @JsonProperty(value="in_xidong")
    private String inArea;
    @JsonProperty(value="road_name")
    private String roadName;

    public EventDetail() {
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getEventTime() {
        return this.eventTime;
    }

    public List<Double> getCoordinates() {
        return this.coordinates;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getSn() {
        return this.sn;
    }

    public long getEventTimestamp() {
        return this.eventTimestamp;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
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

    @JsonProperty(value="event_time")
    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    @JsonProperty(value="coordinates")
    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    @JsonProperty(value="event_type")
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @JsonProperty(value="dongle_id")
    public void setSn(String sn) {
        this.sn = sn;
    }

    @JsonProperty(value="event_timestamp")
    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    @JsonProperty(value="longitude")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty(value="latitude")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
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
        if (!(o instanceof EventDetail)) {
            return false;
        }
        EventDetail other = (EventDetail)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getEventTimestamp() != other.getEventTimestamp()) {
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
        String this$eventTime = this.getEventTime();
        String other$eventTime = other.getEventTime();
        if (this$eventTime == null ? other$eventTime != null : !this$eventTime.equals(other$eventTime)) {
            return false;
        }
        List this$coordinates = this.getCoordinates();
        List other$coordinates = other.getCoordinates();
        if (this$coordinates == null ? other$coordinates != null : !((Object)this$coordinates).equals(other$coordinates)) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        if (this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType)) {
            return false;
        }
        String this$sn = this.getSn();
        String other$sn = other.getSn();
        if (this$sn == null ? other$sn != null : !this$sn.equals(other$sn)) {
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
        return other instanceof EventDetail;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $eventTimestamp = this.getEventTimestamp();
        result = result * 59 + (int)($eventTimestamp >>> 32 ^ $eventTimestamp);
        long $longitude = Double.doubleToLongBits(this.getLongitude());
        result = result * 59 + (int)($longitude >>> 32 ^ $longitude);
        long $latitude = Double.doubleToLongBits(this.getLatitude());
        result = result * 59 + (int)($latitude >>> 32 ^ $latitude);
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $eventTime = this.getEventTime();
        result = result * 59 + ($eventTime == null ? 43 : $eventTime.hashCode());
        List $coordinates = this.getCoordinates();
        result = result * 59 + ($coordinates == null ? 43 : ((Object)$coordinates).hashCode());
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        String $sn = this.getSn();
        result = result * 59 + ($sn == null ? 43 : $sn.hashCode());
        String $inArea = this.getInArea();
        result = result * 59 + ($inArea == null ? 43 : $inArea.hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        return result;
    }

    public String toString() {
        return "EventDetail(status=" + this.getStatus() + ", eventTime=" + this.getEventTime() + ", coordinates=" + this.getCoordinates() + ", eventType=" + this.getEventType() + ", sn=" + this.getSn() + ", eventTimestamp=" + this.getEventTimestamp() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", inArea=" + this.getInArea() + ", roadName=" + this.getRoadName() + ")";
    }
}

