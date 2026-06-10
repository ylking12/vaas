/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report.daily;


public class EventTypeDistDTO {
    private String eventType;
    private Long count;
    private Double proportion;

    public EventTypeDistDTO() {
    }

    public String getEventType() {
        return this.eventType;
    }

    public Long getCount() {
        return this.count;
    }

    public Double getProportion() {
        return this.proportion;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void setProportion(Double proportion) {
        this.proportion = proportion;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventTypeDistDTO)) {
            return false;
        }
        EventTypeDistDTO other = (EventTypeDistDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$count = this.getCount();
        Long other$count = other.getCount();
        if (this$count == null ? other$count != null : !((Object)this$count).equals(other$count)) {
            return false;
        }
        Double this$proportion = this.getProportion();
        Double other$proportion = other.getProportion();
        if (this$proportion == null ? other$proportion != null : !((Object)this$proportion).equals(other$proportion)) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        return !(this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventTypeDistDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $count = this.getCount();
        result = result * 59 + ($count == null ? 43 : ((Object)$count).hashCode());
        Double $proportion = this.getProportion();
        result = result * 59 + ($proportion == null ? 43 : ((Object)$proportion).hashCode());
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        return result;
    }

    public String toString() {
        return "EventTypeDistDTO(eventType=" + this.getEventType() + ", count=" + this.getCount() + ", proportion=" + this.getProportion() + ")";
    }
}

