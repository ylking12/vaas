/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report.daily;


public class CoreIndicatorDTO {
    private Long totalEventCount;
    private Long severeEventCount;
    private Long uniqueRoadCount;

    public CoreIndicatorDTO() {
    }

    public Long getTotalEventCount() {
        return this.totalEventCount;
    }

    public Long getSevereEventCount() {
        return this.severeEventCount;
    }

    public Long getUniqueRoadCount() {
        return this.uniqueRoadCount;
    }

    public void setTotalEventCount(Long totalEventCount) {
        this.totalEventCount = totalEventCount;
    }

    public void setSevereEventCount(Long severeEventCount) {
        this.severeEventCount = severeEventCount;
    }

    public void setUniqueRoadCount(Long uniqueRoadCount) {
        this.uniqueRoadCount = uniqueRoadCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CoreIndicatorDTO)) {
            return false;
        }
        CoreIndicatorDTO other = (CoreIndicatorDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$totalEventCount = this.getTotalEventCount();
        Long other$totalEventCount = other.getTotalEventCount();
        if (this$totalEventCount == null ? other$totalEventCount != null : !((Object)this$totalEventCount).equals(other$totalEventCount)) {
            return false;
        }
        Long this$severeEventCount = this.getSevereEventCount();
        Long other$severeEventCount = other.getSevereEventCount();
        if (this$severeEventCount == null ? other$severeEventCount != null : !((Object)this$severeEventCount).equals(other$severeEventCount)) {
            return false;
        }
        Long this$uniqueRoadCount = this.getUniqueRoadCount();
        Long other$uniqueRoadCount = other.getUniqueRoadCount();
        return !(this$uniqueRoadCount == null ? other$uniqueRoadCount != null : !((Object)this$uniqueRoadCount).equals(other$uniqueRoadCount));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CoreIndicatorDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $totalEventCount = this.getTotalEventCount();
        result = result * 59 + ($totalEventCount == null ? 43 : ((Object)$totalEventCount).hashCode());
        Long $severeEventCount = this.getSevereEventCount();
        result = result * 59 + ($severeEventCount == null ? 43 : ((Object)$severeEventCount).hashCode());
        Long $uniqueRoadCount = this.getUniqueRoadCount();
        result = result * 59 + ($uniqueRoadCount == null ? 43 : ((Object)$uniqueRoadCount).hashCode());
        return result;
    }

    public String toString() {
        return "CoreIndicatorDTO(totalEventCount=" + this.getTotalEventCount() + ", severeEventCount=" + this.getSevereEventCount() + ", uniqueRoadCount=" + this.getUniqueRoadCount() + ")";
    }
}

