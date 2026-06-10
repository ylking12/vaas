/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report.daily;


public class HourlyTrendDTO {
    private Integer hour;
    private String hourRange;
    private Long eventCount;

    public HourlyTrendDTO() {
    }

    public Integer getHour() {
        return this.hour;
    }

    public String getHourRange() {
        return this.hourRange;
    }

    public Long getEventCount() {
        return this.eventCount;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void setHourRange(String hourRange) {
        this.hourRange = hourRange;
    }

    public void setEventCount(Long eventCount) {
        this.eventCount = eventCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HourlyTrendDTO)) {
            return false;
        }
        HourlyTrendDTO other = (HourlyTrendDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$hour = this.getHour();
        Integer other$hour = other.getHour();
        if (this$hour == null ? other$hour != null : !((Object)this$hour).equals(other$hour)) {
            return false;
        }
        Long this$eventCount = this.getEventCount();
        Long other$eventCount = other.getEventCount();
        if (this$eventCount == null ? other$eventCount != null : !((Object)this$eventCount).equals(other$eventCount)) {
            return false;
        }
        String this$hourRange = this.getHourRange();
        String other$hourRange = other.getHourRange();
        return !(this$hourRange == null ? other$hourRange != null : !this$hourRange.equals(other$hourRange));
    }

    protected boolean canEqual(Object other) {
        return other instanceof HourlyTrendDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $hour = this.getHour();
        result = result * 59 + ($hour == null ? 43 : ((Object)$hour).hashCode());
        Long $eventCount = this.getEventCount();
        result = result * 59 + ($eventCount == null ? 43 : ((Object)$eventCount).hashCode());
        String $hourRange = this.getHourRange();
        result = result * 59 + ($hourRange == null ? 43 : $hourRange.hashCode());
        return result;
    }

    public String toString() {
        return "HourlyTrendDTO(hour=" + this.getHour() + ", hourRange=" + this.getHourRange() + ", eventCount=" + this.getEventCount() + ")";
    }
}

