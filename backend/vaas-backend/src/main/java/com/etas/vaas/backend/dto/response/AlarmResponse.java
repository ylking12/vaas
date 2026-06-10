/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.response.AlarmResponse
 */
package com.etas.vaas.backend.dto.response;

import java.time.LocalDateTime;

public class AlarmResponse {
    private String roadName;
    private LocalDateTime datetime;
    private String sourceName;
    private String eventType;

    public AlarmResponse() {
    }

    public String getRoadName() {
        return this.roadName;
    }

    public LocalDateTime getDatetime() {
        return this.datetime;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AlarmResponse)) {
            return false;
        }
        AlarmResponse other = (AlarmResponse)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        if (this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName)) {
            return false;
        }
        LocalDateTime this$datetime = this.getDatetime();
        LocalDateTime other$datetime = other.getDatetime();
        if (this$datetime == null ? other$datetime != null : !((Object)this$datetime).equals(other$datetime)) {
            return false;
        }
        String this$sourceName = this.getSourceName();
        String other$sourceName = other.getSourceName();
        if (this$sourceName == null ? other$sourceName != null : !this$sourceName.equals(other$sourceName)) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        return !(this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AlarmResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        LocalDateTime $datetime = this.getDatetime();
        result = result * 59 + ($datetime == null ? 43 : ((Object)$datetime).hashCode());
        String $sourceName = this.getSourceName();
        result = result * 59 + ($sourceName == null ? 43 : $sourceName.hashCode());
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        return result;
    }

    public String toString() {
        return "AlarmResponse(roadName=" + this.getRoadName() + ", datetime=" + this.getDatetime() + ", sourceName=" + this.getSourceName() + ", eventType=" + this.getEventType() + ")";
    }
}

