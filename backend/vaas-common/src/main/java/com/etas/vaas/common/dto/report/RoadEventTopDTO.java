/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report;


public class RoadEventTopDTO {
    private String roadName;
    private Long eventCount;
    private String mainEventType;

    public RoadEventTopDTO() {
    }

    public String getRoadName() {
        return this.roadName;
    }

    public Long getEventCount() {
        return this.eventCount;
    }

    public String getMainEventType() {
        return this.mainEventType;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public void setEventCount(Long eventCount) {
        this.eventCount = eventCount;
    }

    public void setMainEventType(String mainEventType) {
        this.mainEventType = mainEventType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RoadEventTopDTO)) {
            return false;
        }
        RoadEventTopDTO other = (RoadEventTopDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$eventCount = this.getEventCount();
        Long other$eventCount = other.getEventCount();
        if (this$eventCount == null ? other$eventCount != null : !((Object)this$eventCount).equals(other$eventCount)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        if (this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName)) {
            return false;
        }
        String this$mainEventType = this.getMainEventType();
        String other$mainEventType = other.getMainEventType();
        return !(this$mainEventType == null ? other$mainEventType != null : !this$mainEventType.equals(other$mainEventType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RoadEventTopDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $eventCount = this.getEventCount();
        result = result * 59 + ($eventCount == null ? 43 : ((Object)$eventCount).hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        String $mainEventType = this.getMainEventType();
        result = result * 59 + ($mainEventType == null ? 43 : $mainEventType.hashCode());
        return result;
    }

    public String toString() {
        return "RoadEventTopDTO(roadName=" + this.getRoadName() + ", eventCount=" + this.getEventCount() + ", mainEventType=" + this.getMainEventType() + ")";
    }
}

