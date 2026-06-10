/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report;

import java.time.LocalDate;

public class EventByDateDTO {
    private LocalDate eventDate;
    private String weekDay;
    private Long eventCount;
    private Long validEventCount;

    public EventByDateDTO() {
    }

    public LocalDate getEventDate() {
        return this.eventDate;
    }

    public String getWeekDay() {
        return this.weekDay;
    }

    public Long getEventCount() {
        return this.eventCount;
    }

    public Long getValidEventCount() {
        return this.validEventCount;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public void setEventCount(Long eventCount) {
        this.eventCount = eventCount;
    }

    public void setValidEventCount(Long validEventCount) {
        this.validEventCount = validEventCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventByDateDTO)) {
            return false;
        }
        EventByDateDTO other = (EventByDateDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$eventCount = this.getEventCount();
        Long other$eventCount = other.getEventCount();
        if (this$eventCount == null ? other$eventCount != null : !((Object)this$eventCount).equals(other$eventCount)) {
            return false;
        }
        Long this$validEventCount = this.getValidEventCount();
        Long other$validEventCount = other.getValidEventCount();
        if (this$validEventCount == null ? other$validEventCount != null : !((Object)this$validEventCount).equals(other$validEventCount)) {
            return false;
        }
        LocalDate this$eventDate = this.getEventDate();
        LocalDate other$eventDate = other.getEventDate();
        if (this$eventDate == null ? other$eventDate != null : !((Object)this$eventDate).equals(other$eventDate)) {
            return false;
        }
        String this$weekDay = this.getWeekDay();
        String other$weekDay = other.getWeekDay();
        return !(this$weekDay == null ? other$weekDay != null : !this$weekDay.equals(other$weekDay));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventByDateDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $eventCount = this.getEventCount();
        result = result * 59 + ($eventCount == null ? 43 : ((Object)$eventCount).hashCode());
        Long $validEventCount = this.getValidEventCount();
        result = result * 59 + ($validEventCount == null ? 43 : ((Object)$validEventCount).hashCode());
        LocalDate $eventDate = this.getEventDate();
        result = result * 59 + ($eventDate == null ? 43 : ((Object)$eventDate).hashCode());
        String $weekDay = this.getWeekDay();
        result = result * 59 + ($weekDay == null ? 43 : $weekDay.hashCode());
        return result;
    }

    public String toString() {
        return "EventByDateDTO(eventDate=" + this.getEventDate() + ", weekDay=" + this.getWeekDay() + ", eventCount=" + this.getEventCount() + ", validEventCount=" + this.getValidEventCount() + ")";
    }
}

