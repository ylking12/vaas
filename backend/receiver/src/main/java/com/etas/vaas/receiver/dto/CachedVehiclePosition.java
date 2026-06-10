/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.receiver.dto.CachedVehiclePosition
 */
package com.etas.vaas.receiver.dto;


public class CachedVehiclePosition {
    private Double longitude;
    private Double latitude;
    private Integer speed;
    private Long timestamp;
    private String dateTime;

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Integer getSpeed() {
        return this.speed;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public String getDateTime() {
        return this.dateTime;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CachedVehiclePosition)) {
            return false;
        }
        CachedVehiclePosition other = (CachedVehiclePosition)o;
        if (!other.canEqual((Object)this)) {
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
        Integer this$speed = this.getSpeed();
        Integer other$speed = other.getSpeed();
        if (this$speed == null ? other$speed != null : !((Object)this$speed).equals(other$speed)) {
            return false;
        }
        Long this$timestamp = this.getTimestamp();
        Long other$timestamp = other.getTimestamp();
        if (this$timestamp == null ? other$timestamp != null : !((Object)this$timestamp).equals(other$timestamp)) {
            return false;
        }
        String this$dateTime = this.getDateTime();
        String other$dateTime = other.getDateTime();
        return !(this$dateTime == null ? other$dateTime != null : !this$dateTime.equals(other$dateTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof CachedVehiclePosition;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Integer $speed = this.getSpeed();
        result = result * 59 + ($speed == null ? 43 : ((Object)$speed).hashCode());
        Long $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : ((Object)$timestamp).hashCode());
        String $dateTime = this.getDateTime();
        result = result * 59 + ($dateTime == null ? 43 : $dateTime.hashCode());
        return result;
    }

    public String toString() {
        return "CachedVehiclePosition(longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", speed=" + this.getSpeed() + ", timestamp=" + this.getTimestamp() + ", dateTime=" + this.getDateTime() + ")";
    }

    public CachedVehiclePosition() {
    }

    public CachedVehiclePosition(Double longitude, Double latitude, Integer speed, Long timestamp, String dateTime) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.timestamp = timestamp;
        this.dateTime = dateTime;
    }
}

