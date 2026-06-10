/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector4motion.dto.Statistics
 *  lombok.Generated
 */
package com.etas.vaas.detector4motion.dto;


public class Statistics {
    private Long startTimestamp;
    private Long endTimestamp;
    private Long receivedTimestamp;
    private Long amplitudeAy;
    private Long amplitudeAz;
    private Double maxAmplitudeAy;
    private Double maxAmplitudeAz;

    public Statistics() {
    }

    public Long getStartTimestamp() {
        return this.startTimestamp;
    }

    public Long getEndTimestamp() {
        return this.endTimestamp;
    }

    public Long getReceivedTimestamp() {
        return this.receivedTimestamp;
    }

    public Long getAmplitudeAy() {
        return this.amplitudeAy;
    }

    public Long getAmplitudeAz() {
        return this.amplitudeAz;
    }

    public Double getMaxAmplitudeAy() {
        return this.maxAmplitudeAy;
    }

    public Double getMaxAmplitudeAz() {
        return this.maxAmplitudeAz;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public void setReceivedTimestamp(Long receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public void setAmplitudeAy(Long amplitudeAy) {
        this.amplitudeAy = amplitudeAy;
    }

    public void setAmplitudeAz(Long amplitudeAz) {
        this.amplitudeAz = amplitudeAz;
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
        if (!(o instanceof Statistics)) {
            return false;
        }
        Statistics other = (Statistics)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$startTimestamp = this.getStartTimestamp();
        Long other$startTimestamp = other.getStartTimestamp();
        if (this$startTimestamp == null ? other$startTimestamp != null : !((Object)this$startTimestamp).equals(other$startTimestamp)) {
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
        Long this$amplitudeAy = this.getAmplitudeAy();
        Long other$amplitudeAy = other.getAmplitudeAy();
        if (this$amplitudeAy == null ? other$amplitudeAy != null : !((Object)this$amplitudeAy).equals(other$amplitudeAy)) {
            return false;
        }
        Long this$amplitudeAz = this.getAmplitudeAz();
        Long other$amplitudeAz = other.getAmplitudeAz();
        if (this$amplitudeAz == null ? other$amplitudeAz != null : !((Object)this$amplitudeAz).equals(other$amplitudeAz)) {
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
        return other instanceof Statistics;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $startTimestamp = this.getStartTimestamp();
        result = result * 59 + ($startTimestamp == null ? 43 : ((Object)$startTimestamp).hashCode());
        Long $endTimestamp = this.getEndTimestamp();
        result = result * 59 + ($endTimestamp == null ? 43 : ((Object)$endTimestamp).hashCode());
        Long $receivedTimestamp = this.getReceivedTimestamp();
        result = result * 59 + ($receivedTimestamp == null ? 43 : ((Object)$receivedTimestamp).hashCode());
        Long $amplitudeAy = this.getAmplitudeAy();
        result = result * 59 + ($amplitudeAy == null ? 43 : ((Object)$amplitudeAy).hashCode());
        Long $amplitudeAz = this.getAmplitudeAz();
        result = result * 59 + ($amplitudeAz == null ? 43 : ((Object)$amplitudeAz).hashCode());
        Double $maxAmplitudeAy = this.getMaxAmplitudeAy();
        result = result * 59 + ($maxAmplitudeAy == null ? 43 : ((Object)$maxAmplitudeAy).hashCode());
        Double $maxAmplitudeAz = this.getMaxAmplitudeAz();
        result = result * 59 + ($maxAmplitudeAz == null ? 43 : ((Object)$maxAmplitudeAz).hashCode());
        return result;
    }

    public String toString() {
        return "Statistics(startTimestamp=" + this.getStartTimestamp() + ", endTimestamp=" + this.getEndTimestamp() + ", receivedTimestamp=" + this.getReceivedTimestamp() + ", amplitudeAy=" + this.getAmplitudeAy() + ", amplitudeAz=" + this.getAmplitudeAz() + ", maxAmplitudeAy=" + this.getMaxAmplitudeAy() + ", maxAmplitudeAz=" + this.getMaxAmplitudeAz() + ")";
    }
}

