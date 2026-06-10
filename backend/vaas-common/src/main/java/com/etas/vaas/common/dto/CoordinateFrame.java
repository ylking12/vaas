/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto;


public class CoordinateFrame {
    private String deviceId;
    private Double longitude;
    private Double latitude;
    private Long timestamp;

    public CoordinateFrame() {
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CoordinateFrame)) {
            return false;
        }
        CoordinateFrame other = (CoordinateFrame)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Double this$longitude = this.getLongitude();
        Double other$longitude = other.getLongitude();
        if (this$longitude == null ? other$longitude != null : !((Object)this$longitude).equals(other$longitude)) {
            return false;
        }
        Double this$latitude = this.getLatitude();
        Double other$latitude = other.getLatitude();
        if (this$latitude == null ? other$latitude != null : !((Object)this$latitude).equals(other$latitude)) {
            return false;
        }
        Long this$timestamp = this.getTimestamp();
        Long other$timestamp = other.getTimestamp();
        if (this$timestamp == null ? other$timestamp != null : !((Object)this$timestamp).equals(other$timestamp)) {
            return false;
        }
        String this$deviceId = this.getDeviceId();
        String other$deviceId = other.getDeviceId();
        return !(this$deviceId == null ? other$deviceId != null : !this$deviceId.equals(other$deviceId));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CoordinateFrame;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Long $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : ((Object)$timestamp).hashCode());
        String $deviceId = this.getDeviceId();
        result = result * 59 + ($deviceId == null ? 43 : $deviceId.hashCode());
        return result;
    }

    public String toString() {
        return "CoordinateFrame(deviceId=" + this.getDeviceId() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", timestamp=" + this.getTimestamp() + ")";
    }
}

