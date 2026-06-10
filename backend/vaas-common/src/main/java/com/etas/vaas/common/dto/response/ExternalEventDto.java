/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalEventDto {
    @JsonProperty(value="id")
    private String eventId;
    private String eventType;
    private String sourceId;
    private String sourceType;
    private String roadName;
    @JsonProperty(value="lon")
    private Double longitude;
    @JsonProperty(value="lat")
    private Double latitude;
    private String eventTime;
    private String h3Hash;
    private Integer level;

    public ExternalEventDto() {
    }

    public String getEventId() {
        return this.eventId;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public String getSourceType() {
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

    public String getEventTime() {
        return this.eventTime;
    }

    public String getH3Hash() {
        return this.h3Hash;
    }

    public Integer getLevel() {
        return this.level;
    }

    @JsonProperty(value="id")
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    @JsonProperty(value="lon")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty(value="lat")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setH3Hash(String h3Hash) {
        this.h3Hash = h3Hash;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ExternalEventDto)) {
            return false;
        }
        ExternalEventDto other = (ExternalEventDto)o;
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
        Integer this$level = this.getLevel();
        Integer other$level = other.getLevel();
        if (this$level == null ? other$level != null : !((Object)this$level).equals(other$level)) {
            return false;
        }
        String this$eventId = this.getEventId();
        String other$eventId = other.getEventId();
        if (this$eventId == null ? other$eventId != null : !this$eventId.equals(other$eventId)) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        if (this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType)) {
            return false;
        }
        String this$sourceId = this.getSourceId();
        String other$sourceId = other.getSourceId();
        if (this$sourceId == null ? other$sourceId != null : !this$sourceId.equals(other$sourceId)) {
            return false;
        }
        String this$sourceType = this.getSourceType();
        String other$sourceType = other.getSourceType();
        if (this$sourceType == null ? other$sourceType != null : !this$sourceType.equals(other$sourceType)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        if (this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName)) {
            return false;
        }
        String this$eventTime = this.getEventTime();
        String other$eventTime = other.getEventTime();
        if (this$eventTime == null ? other$eventTime != null : !this$eventTime.equals(other$eventTime)) {
            return false;
        }
        String this$h3Hash = this.getH3Hash();
        String other$h3Hash = other.getH3Hash();
        return !(this$h3Hash == null ? other$h3Hash != null : !this$h3Hash.equals(other$h3Hash));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ExternalEventDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Integer $level = this.getLevel();
        result = result * 59 + ($level == null ? 43 : ((Object)$level).hashCode());
        String $eventId = this.getEventId();
        result = result * 59 + ($eventId == null ? 43 : $eventId.hashCode());
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        String $sourceId = this.getSourceId();
        result = result * 59 + ($sourceId == null ? 43 : $sourceId.hashCode());
        String $sourceType = this.getSourceType();
        result = result * 59 + ($sourceType == null ? 43 : $sourceType.hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        String $eventTime = this.getEventTime();
        result = result * 59 + ($eventTime == null ? 43 : $eventTime.hashCode());
        String $h3Hash = this.getH3Hash();
        result = result * 59 + ($h3Hash == null ? 43 : $h3Hash.hashCode());
        return result;
    }

    public String toString() {
        return "ExternalEventDto(eventId=" + this.getEventId() + ", eventType=" + this.getEventType() + ", sourceId=" + this.getSourceId() + ", sourceType=" + this.getSourceType() + ", roadName=" + this.getRoadName() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", eventTime=" + this.getEventTime() + ", h3Hash=" + this.getH3Hash() + ", level=" + this.getLevel() + ")";
    }
}

