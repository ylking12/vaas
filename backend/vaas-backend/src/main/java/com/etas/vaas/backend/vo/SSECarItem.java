/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.vo.SSECarItem
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.backend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SSECarItem {
    @JsonProperty(value="coordinates")
    private List<Double> coordinate;
    @JsonProperty(value="isOnline")
    private Integer isOnline;
    @JsonProperty(value="vin")
    private String vin;
    @JsonProperty(value="uploaded_event_count")
    private int eventCount;
    @JsonProperty(value="FRWheelSpd")
    private Double speed;

    public SSECarItem() {
    }

    public List<Double> getCoordinate() {
        return this.coordinate;
    }

    public Integer getIsOnline() {
        return this.isOnline;
    }

    public String getVin() {
        return this.vin;
    }

    public int getEventCount() {
        return this.eventCount;
    }

    public Double getSpeed() {
        return this.speed;
    }

    @JsonProperty(value="coordinates")
    public void setCoordinate(List<Double> coordinate) {
        this.coordinate = coordinate;
    }

    @JsonProperty(value="isOnline")
    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }

    @JsonProperty(value="vin")
    public void setVin(String vin) {
        this.vin = vin;
    }

    @JsonProperty(value="uploaded_event_count")
    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    @JsonProperty(value="FRWheelSpd")
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SSECarItem)) {
            return false;
        }
        SSECarItem other = (SSECarItem)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getEventCount() != other.getEventCount()) {
            return false;
        }
        Integer this$isOnline = this.getIsOnline();
        Integer other$isOnline = other.getIsOnline();
        if (this$isOnline == null ? other$isOnline != null : !((Object)this$isOnline).equals(other$isOnline)) {
            return false;
        }
        Double this$speed = this.getSpeed();
        Double other$speed = other.getSpeed();
        if (this$speed == null ? other$speed != null : !((Object)this$speed).equals(other$speed)) {
            return false;
        }
        List this$coordinate = this.getCoordinate();
        List other$coordinate = other.getCoordinate();
        if (this$coordinate == null ? other$coordinate != null : !((Object)this$coordinate).equals(other$coordinate)) {
            return false;
        }
        String this$vin = this.getVin();
        String other$vin = other.getVin();
        return !(this$vin == null ? other$vin != null : !this$vin.equals(other$vin));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SSECarItem;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getEventCount();
        Integer $isOnline = this.getIsOnline();
        result = result * 59 + ($isOnline == null ? 43 : ((Object)$isOnline).hashCode());
        Double $speed = this.getSpeed();
        result = result * 59 + ($speed == null ? 43 : ((Object)$speed).hashCode());
        List $coordinate = this.getCoordinate();
        result = result * 59 + ($coordinate == null ? 43 : ((Object)$coordinate).hashCode());
        String $vin = this.getVin();
        result = result * 59 + ($vin == null ? 43 : $vin.hashCode());
        return result;
    }

    public String toString() {
        return "SSECarItem(coordinate=" + this.getCoordinate() + ", isOnline=" + this.getIsOnline() + ", vin=" + this.getVin() + ", eventCount=" + this.getEventCount() + ", speed=" + this.getSpeed() + ")";
    }
}

