/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dao;

import com.etas.vaas.common.enums.EventType;

public class WeatherEventDao {
    private String sourceId;
    private String eventId;
    private EventType eventType;
    private Long eventTimestamp;
    private String roadName;
    private Double longitude;
    private Double latitude;

    public WeatherEventDao() {
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public String getEventId() {
        return this.eventId;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public Long getEventTimestamp() {
        return this.eventTimestamp;
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

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setEventTimestamp(Long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WeatherEventDao)) {
            return false;
        }
        WeatherEventDao other = (WeatherEventDao)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$eventTimestamp = this.getEventTimestamp();
        Long other$eventTimestamp = other.getEventTimestamp();
        if (this$eventTimestamp == null ? other$eventTimestamp != null : !((Object)this$eventTimestamp).equals(other$eventTimestamp)) {
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
        String this$sourceId = this.getSourceId();
        String other$sourceId = other.getSourceId();
        if (this$sourceId == null ? other$sourceId != null : !this$sourceId.equals(other$sourceId)) {
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
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        return !(this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof WeatherEventDao;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $eventTimestamp = this.getEventTimestamp();
        result = result * 59 + ($eventTimestamp == null ? 43 : ((Object)$eventTimestamp).hashCode());
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        String $sourceId = this.getSourceId();
        result = result * 59 + ($sourceId == null ? 43 : $sourceId.hashCode());
        String $eventId = this.getEventId();
        result = result * 59 + ($eventId == null ? 43 : $eventId.hashCode());
        EventType $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : ((Object)((Object)$eventType)).hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        return result;
    }

    public String toString() {
        return "WeatherEventDao(sourceId=" + this.getSourceId() + ", eventId=" + this.getEventId() + ", eventType=" + this.getEventType() + ", eventTimestamp=" + this.getEventTimestamp() + ", roadName=" + this.getRoadName() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ")";
    }
}

