/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector4motion.dto.IntermediateResult4Motion
 *  lombok.Generated
 */
package com.etas.vaas.detector4motion.dto;


public class IntermediateResult4Motion {
    private Integer status;
    private Long endTimestamp;
    private Long receivedTimestamp;
    private Double maxAmplitudeAy;
    private Double maxAmplitudeAz;

    public IntermediateResult4Motion() {
    }

    public Integer getStatus() {
        return this.status;
    }

    public Long getEndTimestamp() {
        return this.endTimestamp;
    }

    public Long getReceivedTimestamp() {
        return this.receivedTimestamp;
    }

    public Double getMaxAmplitudeAy() {
        return this.maxAmplitudeAy;
    }

    public Double getMaxAmplitudeAz() {
        return this.maxAmplitudeAz;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setReceivedTimestamp(Long receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public void setMaxAmplitudeAy(Double maxAmplitudeAy) {
        this.maxAmplitudeAy = maxAmplitudeAy;
    }

    public void setMaxAmplitudeAz(Double maxAmplitudeAz) {
        this.maxAmplitudeAz = maxAmplitudeAz;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IntermediateResult4Motion)) {
            return false;
        }
        IntermediateResult4Motion other = (IntermediateResult4Motion)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        Long this$endTimestamp = this.getEndTimestamp();
        Long other$endTimestamp = other.getEndTimestamp();
        if (this$endTimestamp == null ? other$endTimestamp != null : !((Object)this$endTimestamp).equals(other$endTimestamp)) {
            return false;
        }
        Long this$receivedTimestamp = this.getReceivedTimestamp();
        Long other$receivedTimestamp = other.getReceivedTimestamp();
        if (this$receivedTimestamp == null ? other$receivedTimestamp != null : !((Object)this$receivedTimestamp).equals(other$receivedTimestamp)) {
            return false;
        }
        Double this$maxAmplitudeAy = this.getMaxAmplitudeAy();
        Double other$maxAmplitudeAy = other.getMaxAmplitudeAy();
        if (this$maxAmplitudeAy == null ? other$maxAmplitudeAy != null : !((Object)this$maxAmplitudeAy).equals(other$maxAmplitudeAy)) {
            return false;
        }
        Double this$maxAmplitudeAz = this.getMaxAmplitudeAz();
        Double other$maxAmplitudeAz = other.getMaxAmplitudeAz();
        return !(this$maxAmplitudeAz == null ? other$maxAmplitudeAz != null : !((Object)this$maxAmplitudeAz).equals(other$maxAmplitudeAz));
    }

    protected boolean canEqual(Object other) {
        return other instanceof IntermediateResult4Motion;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        Long $endTimestamp = this.getEndTimestamp();
        result = result * 59 + ($endTimestamp == null ? 43 : ((Object)$endTimestamp).hashCode());
        Long $receivedTimestamp = this.getReceivedTimestamp();
        result = result * 59 + ($receivedTimestamp == null ? 43 : ((Object)$receivedTimestamp).hashCode());
        Double $maxAmplitudeAy = this.getMaxAmplitudeAy();
        result = result * 59 + ($maxAmplitudeAy == null ? 43 : ((Object)$maxAmplitudeAy).hashCode());
        Double $maxAmplitudeAz = this.getMaxAmplitudeAz();
        result = result * 59 + ($maxAmplitudeAz == null ? 43 : ((Object)$maxAmplitudeAz).hashCode());
        return result;
    }

    public String toString() {
        return "IntermediateResult4Motion(status=" + this.getStatus() + ", endTimestamp=" + this.getEndTimestamp() + ", receivedTimestamp=" + this.getReceivedTimestamp() + ", maxAmplitudeAy=" + this.getMaxAmplitudeAy() + ", maxAmplitudeAz=" + this.getMaxAmplitudeAz() + ")";
    }
}

