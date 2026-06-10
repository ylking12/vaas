/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report;


public class EventByTimeSlotDTO {
    private String timeSlot;
    private Long eventCount;
    private Double proportion;

    public EventByTimeSlotDTO() {
    }

    public String getTimeSlot() {
        return this.timeSlot;
    }

    public Long getEventCount() {
        return this.eventCount;
    }

    public Double getProportion() {
        return this.proportion;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setEventCount(Long eventCount) {
        this.eventCount = eventCount;
    }

    public void setProportion(Double proportion) {
        this.proportion = proportion;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventByTimeSlotDTO)) {
            return false;
        }
        EventByTimeSlotDTO other = (EventByTimeSlotDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$eventCount = this.getEventCount();
        Long other$eventCount = other.getEventCount();
        if (this$eventCount == null ? other$eventCount != null : !((Object)this$eventCount).equals(other$eventCount)) {
            return false;
        }
        Double this$proportion = this.getProportion();
        Double other$proportion = other.getProportion();
        if (this$proportion == null ? other$proportion != null : !((Object)this$proportion).equals(other$proportion)) {
            return false;
        }
        String this$timeSlot = this.getTimeSlot();
        String other$timeSlot = other.getTimeSlot();
        return !(this$timeSlot == null ? other$timeSlot != null : !this$timeSlot.equals(other$timeSlot));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventByTimeSlotDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $eventCount = this.getEventCount();
        result = result * 59 + ($eventCount == null ? 43 : ((Object)$eventCount).hashCode());
        Double $proportion = this.getProportion();
        result = result * 59 + ($proportion == null ? 43 : ((Object)$proportion).hashCode());
        String $timeSlot = this.getTimeSlot();
        result = result * 59 + ($timeSlot == null ? 43 : $timeSlot.hashCode());
        return result;
    }

    public String toString() {
        return "EventByTimeSlotDTO(timeSlot=" + this.getTimeSlot() + ", eventCount=" + this.getEventCount() + ", proportion=" + this.getProportion() + ")";
    }
}

