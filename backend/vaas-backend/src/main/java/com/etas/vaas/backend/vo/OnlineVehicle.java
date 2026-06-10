/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.vo.OnlineVehicle
 *  com.etas.vaas.backend.vo.OnlineVehicle$Coordinates
 */
package com.etas.vaas.backend.vo;

import com.etas.vaas.backend.vo.OnlineVehicle;

public class OnlineVehicle {
    private String vehicleId;
    private Coordinates coordinates;
    private String plateNumber;
    private String deviceId;
    private String serialNumber;
    private Integer eventCount;
    private Integer speed;

    public OnlineVehicle() {
    }

    public String getVehicleId() {
        return this.vehicleId;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public Integer getEventCount() {
        return this.eventCount;
    }

    public Integer getSpeed() {
        return this.speed;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setEventCount(Integer eventCount) {
        this.eventCount = eventCount;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OnlineVehicle)) {
            return false;
        }
        OnlineVehicle other = (OnlineVehicle)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$eventCount = this.getEventCount();
        Integer other$eventCount = other.getEventCount();
        if (this$eventCount == null ? other$eventCount != null : !((Object)this$eventCount).equals(other$eventCount)) {
            return false;
        }
        Integer this$speed = this.getSpeed();
        Integer other$speed = other.getSpeed();
        if (this$speed == null ? other$speed != null : !((Object)this$speed).equals(other$speed)) {
            return false;
        }
        String this$vehicleId = this.getVehicleId();
        String other$vehicleId = other.getVehicleId();
        if (this$vehicleId == null ? other$vehicleId != null : !this$vehicleId.equals(other$vehicleId)) {
            return false;
        }
        Coordinates this$coordinates = this.getCoordinates();
        Coordinates other$coordinates = other.getCoordinates();
        if (this$coordinates == null ? other$coordinates != null : !this$coordinates.equals(other$coordinates)) {
            return false;
        }
        String this$plateNumber = this.getPlateNumber();
        String other$plateNumber = other.getPlateNumber();
        if (this$plateNumber == null ? other$plateNumber != null : !this$plateNumber.equals(other$plateNumber)) {
            return false;
        }
        String this$deviceId = this.getDeviceId();
        String other$deviceId = other.getDeviceId();
        if (this$deviceId == null ? other$deviceId != null : !this$deviceId.equals(other$deviceId)) {
            return false;
        }
        String this$serialNumber = this.getSerialNumber();
        String other$serialNumber = other.getSerialNumber();
        return !(this$serialNumber == null ? other$serialNumber != null : !this$serialNumber.equals(other$serialNumber));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OnlineVehicle;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $eventCount = this.getEventCount();
        result = result * 59 + ($eventCount == null ? 43 : ((Object)$eventCount).hashCode());
        Integer $speed = this.getSpeed();
        result = result * 59 + ($speed == null ? 43 : ((Object)$speed).hashCode());
        String $vehicleId = this.getVehicleId();
        result = result * 59 + ($vehicleId == null ? 43 : $vehicleId.hashCode());
        Coordinates $coordinates = this.getCoordinates();
        result = result * 59 + ($coordinates == null ? 43 : $coordinates.hashCode());
        String $plateNumber = this.getPlateNumber();
        result = result * 59 + ($plateNumber == null ? 43 : $plateNumber.hashCode());
        String $deviceId = this.getDeviceId();
        result = result * 59 + ($deviceId == null ? 43 : $deviceId.hashCode());
        String $serialNumber = this.getSerialNumber();
        result = result * 59 + ($serialNumber == null ? 43 : $serialNumber.hashCode());
        return result;
    }

    public String toString() {
        return "OnlineVehicle(vehicleId=" + this.getVehicleId() + ", coordinates=" + this.getCoordinates() + ", plateNumber=" + this.getPlateNumber() + ", deviceId=" + this.getDeviceId() + ", serialNumber=" + this.getSerialNumber() + ", eventCount=" + this.getEventCount() + ", speed=" + this.getSpeed() + ")";
    }
}

