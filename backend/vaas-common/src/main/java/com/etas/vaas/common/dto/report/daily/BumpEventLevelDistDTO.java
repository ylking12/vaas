/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report.daily;


public class BumpEventLevelDistDTO {
    private Integer level;
    private Long count;
    private Double proportion;

    public BumpEventLevelDistDTO() {
    }

    public Integer getLevel() {
        return this.level;
    }

    public Long getCount() {
        return this.count;
    }

    public Double getProportion() {
        return this.proportion;
    }

    public void setLevel(Integer level) {
        this.level = level;
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
        if (!(o instanceof BumpEventLevelDistDTO)) {
            return false;
        }
        BumpEventLevelDistDTO other = (BumpEventLevelDistDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$level = this.getLevel();
        Integer other$level = other.getLevel();
        if (this$level == null ? other$level != null : !((Object)this$level).equals(other$level)) {
            return false;
        }
        Long this$count = this.getCount();
        Long other$count = other.getCount();
        if (this$count == null ? other$count != null : !((Object)this$count).equals(other$count)) {
            return false;
        }
        Double this$proportion = this.getProportion();
        Double other$proportion = other.getProportion();
        return !(this$proportion == null ? other$proportion != null : !((Object)this$proportion).equals(other$proportion));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BumpEventLevelDistDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $level = this.getLevel();
        result = result * 59 + ($level == null ? 43 : ((Object)$level).hashCode());
        Long $count = this.getCount();
        result = result * 59 + ($count == null ? 43 : ((Object)$count).hashCode());
        Double $proportion = this.getProportion();
        result = result * 59 + ($proportion == null ? 43 : ((Object)$proportion).hashCode());
        return result;
    }

    public String toString() {
        return "BumpEventLevelDistDTO(level=" + this.getLevel() + ", count=" + this.getCount() + ", proportion=" + this.getProportion() + ")";
    }
}

