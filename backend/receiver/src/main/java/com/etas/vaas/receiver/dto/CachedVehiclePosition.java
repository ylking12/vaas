/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.dto.CachedVehiclePosition | STATUS: Restored */
package com.etas.vaas.receiver.dto;

public class CachedVehiclePosition {
    private Double longitude;
    private Double latitude;
    private Integer speed;
    private Long timestamp;
    private String dateTime;

    public CachedVehiclePosition() {
    }

    public CachedVehiclePosition(Double longitude, Double latitude, Integer speed, Long timestamp, String dateTime) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.timestamp = timestamp;
        this.dateTime = dateTime;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Integer getSpeed() {
        return speed;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getDateTime() {
        return dateTime;
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
}
