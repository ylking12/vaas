/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.response.EventResponse
 *  com.etas.vaas.common.enums.EventType
 */
package com.etas.vaas.backend.dto.response;

import com.etas.vaas.common.enums.EventType;
import java.time.LocalDateTime;

public class EventResponse {
    private String eventId;
    private EventType eventType;
    private Integer level;
    private Double longitude;
    private Double latitude;
    private LocalDateTime eventTime;

    public EventResponse() {
    }

    public String getEventId() {
        return this.eventId;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public Integer getLevel() {
        return this.level;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public LocalDateTime getEventTime() {
        return this.eventTime;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventResponse)) {
            return false;
        }
        EventResponse other = (EventResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$level = this.getLevel();
        Integer other$level = other.getLevel();
        if (this$level == null ? other$level != null : !((Object)this$level).equals(other$level)) {
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
        EventType this$eventType = this.getEventType();
        EventType other$eventType = other.getEventType();
        if (this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType)) {
            return false;
        }
        LocalDateTime this$eventTime = this.getEventTime();
        LocalDateTime other$eventTime = other.getEventTime();
        return !(this$eventTime == null ? other$eventTime != null : !((Object)this$eventTime).equals(other$eventTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $level = this.getLevel();
        result = result * 59 + ($level == null ? 43 : ((Object)$level).hashCode());
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        String $eventId = this.getEventId();
        result = result * 59 + ($eventId == null ? 43 : $eventId.hashCode());
        EventType $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        LocalDateTime $eventTime = this.getEventTime();
        result = result * 59 + ($eventTime == null ? 43 : ((Object)$eventTime).hashCode());
        return result;
    }

    public String toString() {
        return "EventResponse(eventId=" + this.getEventId() + ", eventType=" + this.getEventType() + ", level=" + this.getLevel() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", eventTime=" + this.getEventTime() + ")";
    }
}

