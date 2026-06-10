/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto;


public class CachedEventDto {
    private String eventId;
    private String deviceId;
    private String deviceType;
    private String eventType;
    private String eventTime;
    private Long eventTimestamp;
    private Long receivedTimestamp;
    private Long perceptionTimestamp;
    private Double longitude;
    private Double latitude;
    private String roadName;

    public CachedEventDto() {
    }

    public String getEventId() {
        return this.eventId;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getEventTime() {
        return this.eventTime;
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

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public String getRoadName() {
        return this.roadName;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
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

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CachedEventDto)) {
            return false;
        }
        CachedEventDto other = (CachedEventDto)o;
        if (!other.canEqual(this)) {
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
        String this$eventId = this.getEventId();
        String other$eventId = other.getEventId();
        if (this$eventId == null ? other$eventId != null : !this$eventId.equals(other$eventId)) {
            return false;
        }
        String this$deviceId = this.getDeviceId();
        String other$deviceId = other.getDeviceId();
        if (this$deviceId == null ? other$deviceId != null : !this$deviceId.equals(other$deviceId)) {
            return false;
        }
        String this$deviceType = this.getDeviceType();
        String other$deviceType = other.getDeviceType();
        if (this$deviceType == null ? other$deviceType != null : !this$deviceType.equals(other$deviceType)) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        if (this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType)) {
            return false;
        }
        String this$eventTime = this.getEventTime();
        String other$eventTime = other.getEventTime();
        if (this$eventTime == null ? other$eventTime != null : !this$eventTime.equals(other$eventTime)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        return !(this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CachedEventDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $eventTimestamp = this.getEventTimestamp();
        result = result * 59 + ($eventTimestamp == null ? 43 : ((Object)$eventTimestamp).hashCode());
        Long $receivedTimestamp = this.getReceivedTimestamp();
        result = result * 59 + ($receivedTimestamp == null ? 43 : ((Object)$receivedTimestamp).hashCode());
        Long $perceptionTimestamp = this.getPerceptionTimestamp();
        result = result * 59 + ($perceptionTimestamp == null ? 43 : ((Object)$perceptionTimestamp).hashCode());
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        String $eventId = this.getEventId();
        result = result * 59 + ($eventId == null ? 43 : $eventId.hashCode());
        String $deviceId = this.getDeviceId();
        result = result * 59 + ($deviceId == null ? 43 : $deviceId.hashCode());
        String $deviceType = this.getDeviceType();
        result = result * 59 + ($deviceType == null ? 43 : $deviceType.hashCode());
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        String $eventTime = this.getEventTime();
        result = result * 59 + ($eventTime == null ? 43 : $eventTime.hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        return result;
    }

    public String toString() {
        return "CachedEventDto(eventId=" + this.getEventId() + ", deviceId=" + this.getDeviceId() + ", deviceType=" + this.getDeviceType() + ", eventType=" + this.getEventType() + ", eventTime=" + this.getEventTime() + ", eventTimestamp=" + this.getEventTimestamp() + ", receivedTimestamp=" + this.getReceivedTimestamp() + ", perceptionTimestamp=" + this.getPerceptionTimestamp() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", roadName=" + this.getRoadName() + ")";
    }
}

