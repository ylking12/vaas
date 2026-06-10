/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report.daily;


public class RoadTopDTO {
    private Integer rank;
    private String roadName;
    private Long eventCount;

    public RoadTopDTO() {
    }

    public Integer getRank() {
        return this.rank;
    }

    public String getRoadName() {
        return this.roadName;
    }

    public Long getEventCount() {
        return this.eventCount;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public void setEventCount(Long eventCount) {
        this.eventCount = eventCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RoadTopDTO)) {
            return false;
        }
        RoadTopDTO other = (RoadTopDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$rank = this.getRank();
        Integer other$rank = other.getRank();
        if (this$rank == null ? other$rank != null : !((Object)this$rank).equals(other$rank)) {
            return false;
        }
        Long this$eventCount = this.getEventCount();
        Long other$eventCount = other.getEventCount();
        if (this$eventCount == null ? other$eventCount != null : !((Object)this$eventCount).equals(other$eventCount)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        return !(this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RoadTopDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $rank = this.getRank();
        result = result * 59 + ($rank == null ? 43 : ((Object)$rank).hashCode());
        Long $eventCount = this.getEventCount();
        result = result * 59 + ($eventCount == null ? 43 : ((Object)$eventCount).hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        return result;
    }

    public String toString() {
        return "RoadTopDTO(rank=" + this.getRank() + ", roadName=" + this.getRoadName() + ", eventCount=" + this.getEventCount() + ")";
    }
}

