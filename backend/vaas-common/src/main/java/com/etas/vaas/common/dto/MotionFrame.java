/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 */
package com.etas.vaas.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MotionFrame {
    private String deviceId;
    private Double aX;
    private Double aY;
    private Double aZ;
    private Double wX;
    private Double wY;
    private Double wZ;
    private Long timestamp;
    private Long receivedTimestamp;

    public MotionFrame() {
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public Double getAX() {
        return this.aX;
    }

    public Double getAY() {
        return this.aY;
    }

    public Double getAZ() {
        return this.aZ;
    }

    public Double getWX() {
        return this.wX;
    }

    public Double getWY() {
        return this.wY;
    }

    public Double getWZ() {
        return this.wZ;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public Long getReceivedTimestamp() {
        return this.receivedTimestamp;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setAX(Double aX) {
        this.aX = aX;
    }

    public void setAY(Double aY) {
        this.aY = aY;
    }

    public void setAZ(Double aZ) {
        this.aZ = aZ;
    }

    public void setWX(Double wX) {
        this.wX = wX;
    }

    public void setWY(Double wY) {
        this.wY = wY;
    }

    public void setWZ(Double wZ) {
        this.wZ = wZ;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setReceivedTimestamp(Long receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MotionFrame)) {
            return false;
        }
        MotionFrame other = (MotionFrame)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Double this$aX = this.getAX();
        Double other$aX = other.getAX();
        if (this$aX == null ? other$aX != null : !((Object)this$aX).equals(other$aX)) {
            return false;
        }
        Double this$aY = this.getAY();
        Double other$aY = other.getAY();
        if (this$aY == null ? other$aY != null : !((Object)this$aY).equals(other$aY)) {
            return false;
        }
        Double this$aZ = this.getAZ();
        Double other$aZ = other.getAZ();
        if (this$aZ == null ? other$aZ != null : !((Object)this$aZ).equals(other$aZ)) {
            return false;
        }
        Double this$wX = this.getWX();
        Double other$wX = other.getWX();
        if (this$wX == null ? other$wX != null : !((Object)this$wX).equals(other$wX)) {
            return false;
        }
        Double this$wY = this.getWY();
        Double other$wY = other.getWY();
        if (this$wY == null ? other$wY != null : !((Object)this$wY).equals(other$wY)) {
            return false;
        }
        Double this$wZ = this.getWZ();
        Double other$wZ = other.getWZ();
        if (this$wZ == null ? other$wZ != null : !((Object)this$wZ).equals(other$wZ)) {
            return false;
        }
        Long this$timestamp = this.getTimestamp();
        Long other$timestamp = other.getTimestamp();
        if (this$timestamp == null ? other$timestamp != null : !((Object)this$timestamp).equals(other$timestamp)) {
            return false;
        }
        Long this$receivedTimestamp = this.getReceivedTimestamp();
        Long other$receivedTimestamp = other.getReceivedTimestamp();
        if (this$receivedTimestamp == null ? other$receivedTimestamp != null : !((Object)this$receivedTimestamp).equals(other$receivedTimestamp)) {
            return false;
        }
        String this$deviceId = this.getDeviceId();
        String other$deviceId = other.getDeviceId();
        return !(this$deviceId == null ? other$deviceId != null : !this$deviceId.equals(other$deviceId));
    }

    protected boolean canEqual(Object other) {
        return other instanceof MotionFrame;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $aX = this.getAX();
        result = result * 59 + ($aX == null ? 43 : ((Object)$aX).hashCode());
        Double $aY = this.getAY();
        result = result * 59 + ($aY == null ? 43 : ((Object)$aY).hashCode());
        Double $aZ = this.getAZ();
        result = result * 59 + ($aZ == null ? 43 : ((Object)$aZ).hashCode());
        Double $wX = this.getWX();
        result = result * 59 + ($wX == null ? 43 : ((Object)$wX).hashCode());
        Double $wY = this.getWY();
        result = result * 59 + ($wY == null ? 43 : ((Object)$wY).hashCode());
        Double $wZ = this.getWZ();
        result = result * 59 + ($wZ == null ? 43 : ((Object)$wZ).hashCode());
        Long $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : ((Object)$timestamp).hashCode());
        Long $receivedTimestamp = this.getReceivedTimestamp();
        result = result * 59 + ($receivedTimestamp == null ? 43 : ((Object)$receivedTimestamp).hashCode());
        String $deviceId = this.getDeviceId();
        result = result * 59 + ($deviceId == null ? 43 : $deviceId.hashCode());
        return result;
    }

    public String toString() {
        return "MotionFrame(deviceId=" + this.getDeviceId() + ", aX=" + this.getAX() + ", aY=" + this.getAY() + ", aZ=" + this.getAZ() + ", wX=" + this.getWX() + ", wY=" + this.getWY() + ", wZ=" + this.getWZ() + ", timestamp=" + this.getTimestamp() + ", receivedTimestamp=" + this.getReceivedTimestamp() + ")";
    }
}

