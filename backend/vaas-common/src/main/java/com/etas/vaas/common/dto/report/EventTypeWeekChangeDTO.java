/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report;


public class EventTypeWeekChangeDTO {
    private String eventType;
    private String eventTypeCn;
    private Long currentWeekCount;
    private Long lastWeekCount;
    private Double changeRate;

    public EventTypeWeekChangeDTO() {
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getEventTypeCn() {
        return this.eventTypeCn;
    }

    public Long getCurrentWeekCount() {
        return this.currentWeekCount;
    }

    public Long getLastWeekCount() {
        return this.lastWeekCount;
    }

    public Double getChangeRate() {
        return this.changeRate;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventTypeCn(String eventTypeCn) {
        this.eventTypeCn = eventTypeCn;
    }

    public void setCurrentWeekCount(Long currentWeekCount) {
        this.currentWeekCount = currentWeekCount;
    }

    public void setLastWeekCount(Long lastWeekCount) {
        this.lastWeekCount = lastWeekCount;
    }

    public void setChangeRate(Double changeRate) {
        this.changeRate = changeRate;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventTypeWeekChangeDTO)) {
            return false;
        }
        EventTypeWeekChangeDTO other = (EventTypeWeekChangeDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$currentWeekCount = this.getCurrentWeekCount();
        Long other$currentWeekCount = other.getCurrentWeekCount();
        if (this$currentWeekCount == null ? other$currentWeekCount != null : !((Object)this$currentWeekCount).equals(other$currentWeekCount)) {
            return false;
        }
        Long this$lastWeekCount = this.getLastWeekCount();
        Long other$lastWeekCount = other.getLastWeekCount();
        if (this$lastWeekCount == null ? other$lastWeekCount != null : !((Object)this$lastWeekCount).equals(other$lastWeekCount)) {
            return false;
        }
        Double this$changeRate = this.getChangeRate();
        Double other$changeRate = other.getChangeRate();
        if (this$changeRate == null ? other$changeRate != null : !((Object)this$changeRate).equals(other$changeRate)) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        if (this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType)) {
            return false;
        }
        String this$eventTypeCn = this.getEventTypeCn();
        String other$eventTypeCn = other.getEventTypeCn();
        return !(this$eventTypeCn == null ? other$eventTypeCn != null : !this$eventTypeCn.equals(other$eventTypeCn));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventTypeWeekChangeDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $currentWeekCount = this.getCurrentWeekCount();
        result = result * 59 + ($currentWeekCount == null ? 43 : ((Object)$currentWeekCount).hashCode());
        Long $lastWeekCount = this.getLastWeekCount();
        result = result * 59 + ($lastWeekCount == null ? 43 : ((Object)$lastWeekCount).hashCode());
        Double $changeRate = this.getChangeRate();
        result = result * 59 + ($changeRate == null ? 43 : ((Object)$changeRate).hashCode());
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        String $eventTypeCn = this.getEventTypeCn();
        result = result * 59 + ($eventTypeCn == null ? 43 : $eventTypeCn.hashCode());
        return result;
    }

    public String toString() {
        return "EventTypeWeekChangeDTO(eventType=" + this.getEventType() + ", eventTypeCn=" + this.getEventTypeCn() + ", currentWeekCount=" + this.getCurrentWeekCount() + ", lastWeekCount=" + this.getLastWeekCount() + ", changeRate=" + this.getChangeRate() + ")";
    }
}

