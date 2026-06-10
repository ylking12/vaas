/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.IdType
 *  com.baomidou.mybatisplus.annotation.TableField
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 */
package com.etas.vaas.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.enums.SourceType;
import java.time.LocalDateTime;

@TableName(value="event")
public class Event {
    @TableId(type=IdType.AUTO)
    private Long id;
    @TableField(value="event_id")
    private String eventId;
    @TableField(value="event_type")
    private EventType eventType;
    @TableField(value="source_id")
    private String sourceId;
    @TableField(value="source_type")
    private SourceType sourceType;
    @TableField(value="road_name")
    private String roadName;
    @TableField(value="longitude")
    private Double longitude;
    @TableField(value="latitude")
    private Double latitude;
    @TableField(value="in_area")
    private Boolean inArea;
    @TableField(value="event_time")
    private LocalDateTime eventTime;
    @TableField(value="received_time")
    private LocalDateTime receivedTime;
    @TableField(value="perception_time")
    private LocalDateTime perceptionTime;
    @TableField(value="duplicated")
    private Boolean duplicated;
    @TableField(value="level")
    private Integer level;
    @TableField(value="simulated")
    private Boolean simulated;
    @TableField(value="h3_hash")
    private String h3Hash;

    public Event() {
    }

    public Long getId() {
        return this.id;
    }

    public String getEventId() {
        return this.eventId;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public String getSourceId() {
        return this.sourceId;
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

    public LocalDateTime getEventTime() {
        return this.eventTime;
    }

    public LocalDateTime getReceivedTime() {
        return this.receivedTime;
    }

    public LocalDateTime getPerceptionTime() {
        return this.perceptionTime;
    }

    public Boolean getDuplicated() {
        return this.duplicated;
    }

    public Integer getLevel() {
        return this.level;
    }

    public Boolean getSimulated() {
        return this.simulated;
    }

    public String getH3Hash() {
        return this.h3Hash;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public void setReceivedTime(LocalDateTime receivedTime) {
        this.receivedTime = receivedTime;
    }

    public void setPerceptionTime(LocalDateTime perceptionTime) {
        this.perceptionTime = perceptionTime;
    }

    public void setDuplicated(Boolean duplicated) {
        this.duplicated = duplicated;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setSimulated(Boolean simulated) {
        this.simulated = simulated;
    }

    public void setH3Hash(String h3Hash) {
        this.h3Hash = h3Hash;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        Event other = (Event)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
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
        String this$sourceId = this.getSourceId();
        String other$sourceId = other.getSourceId();
        if (this$sourceId == null ? other$sourceId != null : !this$sourceId.equals(other$sourceId)) {
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
        LocalDateTime this$eventTime = this.getEventTime();
        LocalDateTime other$eventTime = other.getEventTime();
        if (this$eventTime == null ? other$eventTime != null : !((Object)this$eventTime).equals(other$eventTime)) {
            return false;
        }
        LocalDateTime this$receivedTime = this.getReceivedTime();
        LocalDateTime other$receivedTime = other.getReceivedTime();
        if (this$receivedTime == null ? other$receivedTime != null : !((Object)this$receivedTime).equals(other$receivedTime)) {
            return false;
        }
        LocalDateTime this$perceptionTime = this.getPerceptionTime();
        LocalDateTime other$perceptionTime = other.getPerceptionTime();
        if (this$perceptionTime == null ? other$perceptionTime != null : !((Object)this$perceptionTime).equals(other$perceptionTime)) {
            return false;
        }
        String this$h3Hash = this.getH3Hash();
        String other$h3Hash = other.getH3Hash();
        return !(this$h3Hash == null ? other$h3Hash != null : !this$h3Hash.equals(other$h3Hash));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Event;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Boolean $inArea = this.getInArea();
        result = result * 59 + ($inArea == null ? 43 : ((Object)$inArea).hashCode());
        Boolean $duplicated = this.getDuplicated();
        result = result * 59 + ($duplicated == null ? 43 : ((Object)$duplicated).hashCode());
        Integer $level = this.getLevel();
        result = result * 59 + ($level == null ? 43 : ((Object)$level).hashCode());
        Boolean $simulated = this.getSimulated();
        result = result * 59 + ($simulated == null ? 43 : ((Object)$simulated).hashCode());
        String $eventId = this.getEventId();
        result = result * 59 + ($eventId == null ? 43 : $eventId.hashCode());
        EventType $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : ((Object)((Object)$eventType)).hashCode());
        String $sourceId = this.getSourceId();
        result = result * 59 + ($sourceId == null ? 43 : $sourceId.hashCode());
        SourceType $sourceType = this.getSourceType();
        result = result * 59 + ($sourceType == null ? 43 : ((Object)((Object)$sourceType)).hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        LocalDateTime $eventTime = this.getEventTime();
        result = result * 59 + ($eventTime == null ? 43 : ((Object)$eventTime).hashCode());
        LocalDateTime $receivedTime = this.getReceivedTime();
        result = result * 59 + ($receivedTime == null ? 43 : ((Object)$receivedTime).hashCode());
        LocalDateTime $perceptionTime = this.getPerceptionTime();
        result = result * 59 + ($perceptionTime == null ? 43 : ((Object)$perceptionTime).hashCode());
        String $h3Hash = this.getH3Hash();
        result = result * 59 + ($h3Hash == null ? 43 : $h3Hash.hashCode());
        return result;
    }

    public String toString() {
        return "Event(id=" + this.getId() + ", eventId=" + this.getEventId() + ", eventType=" + this.getEventType() + ", sourceId=" + this.getSourceId() + ", sourceType=" + this.getSourceType() + ", roadName=" + this.getRoadName() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", inArea=" + this.getInArea() + ", eventTime=" + this.getEventTime() + ", receivedTime=" + this.getReceivedTime() + ", perceptionTime=" + this.getPerceptionTime() + ", duplicated=" + this.getDuplicated() + ", level=" + this.getLevel() + ", simulated=" + this.getSimulated() + ", h3Hash=" + this.getH3Hash() + ")";
    }
}

