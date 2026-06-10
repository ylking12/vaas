/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  com.fasterxml.jackson.databind.annotation.JsonDeserialize
 *  com.fasterxml.jackson.databind.annotation.JsonSerialize
 *  com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
 *  com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
 *  org.springframework.format.annotation.DateTimeFormat
 */
package com.etas.vaas.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class CachedVehiclePosition
implements Comparable<CachedVehiclePosition> {
    private Double longitude;
    private Double latitude;
    private Integer speed;
    private Long timestamp;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS", timezone="GMT+8", shape=JsonFormat.Shape.STRING)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    private LocalDateTime dateTime;

    public CachedVehiclePosition(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(CachedVehiclePosition o) {
        return Long.compare(this.timestamp, o.getTimestamp());
    }

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

    public LocalDateTime getDateTime() {
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

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS", timezone="GMT+8", shape=JsonFormat.Shape.STRING)
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    public void setDateTime(LocalDateTime dateTime) {
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
        LocalDateTime this$dateTime = this.getDateTime();
        LocalDateTime other$dateTime = other.getDateTime();
        return !(this$dateTime == null ? other$dateTime != null : !((Object)this$dateTime).equals(other$dateTime));
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
        LocalDateTime $dateTime = this.getDateTime();
        result = result * 59 + ($dateTime == null ? 43 : ((Object)$dateTime).hashCode());
        return result;
    }

    public String toString() {
        return "CachedVehiclePosition(longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ", speed=" + this.getSpeed() + ", timestamp=" + this.getTimestamp() + ", dateTime=" + this.getDateTime() + ")";
    }

    public CachedVehiclePosition() {
    }

    public CachedVehiclePosition(Double longitude, Double latitude, Integer speed, Long timestamp, LocalDateTime dateTime) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.timestamp = timestamp;
        this.dateTime = dateTime;
    }
}

