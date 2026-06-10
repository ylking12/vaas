/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector.entity.Frame
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.detector.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Frame {
    @JsonProperty(value="date")
    private String date;
    @JsonProperty(value="sn")
    private String sn;
    @JsonProperty(value="VehicleSpd( Km/h)")
    private Float vehicleSpd;
    @JsonProperty(value="WiperFlag")
    private String wiperFlag;
    @JsonProperty(value="SteerWheelAngle( deg)")
    private Float steerWheelAngle;
    @JsonProperty(value="LateralAcce( m/s^2)")
    private Float lateralAcce;
    @JsonProperty(value="FLWheelSpd( Km/h)")
    private Float flWheelSpd;
    @JsonProperty(value="FRWheelSpd( Km/h)")
    private Float frWheelSpd;
    @JsonProperty(value="ESC_Mcylinder_Pressure( bar)")
    private Float escMcylinderPressure;
    @JsonProperty(value="RLWheelSpd( Km/h)")
    private Float rlWheelSpd;
    @JsonProperty(value="RRWheelSpd( Km/h)")
    private Float rrWheelSpd;
    @JsonProperty(value="LongitudeAcc( m/s^2)")
    private Float longitudeAcc;
    @JsonProperty(value="received_at")
    private String receivedTime;
    private Float deltaTime;
    private Float distance;
    private Float leftSpeedDiff;
    private Float rightSpeedDiff;
    private Float leftSteeringRatio;
    private Float rightSteeringRatio;

    public Frame() {
    }

    public String getDate() {
        return this.date;
    }

    public String getSn() {
        return this.sn;
    }

    public Float getVehicleSpd() {
        return this.vehicleSpd;
    }

    public String getWiperFlag() {
        return this.wiperFlag;
    }

    public Float getSteerWheelAngle() {
        return this.steerWheelAngle;
    }

    public Float getLateralAcce() {
        return this.lateralAcce;
    }

    public Float getFlWheelSpd() {
        return this.flWheelSpd;
    }

    public Float getFrWheelSpd() {
        return this.frWheelSpd;
    }

    public Float getEscMcylinderPressure() {
        return this.escMcylinderPressure;
    }

    public Float getRlWheelSpd() {
        return this.rlWheelSpd;
    }

    public Float getRrWheelSpd() {
        return this.rrWheelSpd;
    }

    public Float getLongitudeAcc() {
        return this.longitudeAcc;
    }

    public String getReceivedTime() {
        return this.receivedTime;
    }

    public Float getDeltaTime() {
        return this.deltaTime;
    }

    public Float getDistance() {
        return this.distance;
    }

    public Float getLeftSpeedDiff() {
        return this.leftSpeedDiff;
    }

    public Float getRightSpeedDiff() {
        return this.rightSpeedDiff;
    }

    public Float getLeftSteeringRatio() {
        return this.leftSteeringRatio;
    }

    public Float getRightSteeringRatio() {
        return this.rightSteeringRatio;
    }

    @JsonProperty(value="date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty(value="sn")
    public void setSn(String sn) {
        this.sn = sn;
    }

    @JsonProperty(value="VehicleSpd( Km/h)")
    public void setVehicleSpd(Float vehicleSpd) {
        this.vehicleSpd = vehicleSpd;
    }

    @JsonProperty(value="WiperFlag")
    public void setWiperFlag(String wiperFlag) {
        this.wiperFlag = wiperFlag;
    }

    @JsonProperty(value="SteerWheelAngle( deg)")
    public void setSteerWheelAngle(Float steerWheelAngle) {
        this.steerWheelAngle = steerWheelAngle;
    }

    @JsonProperty(value="LateralAcce( m/s^2)")
    public void setLateralAcce(Float lateralAcce) {
        this.lateralAcce = lateralAcce;
    }

    @JsonProperty(value="FLWheelSpd( Km/h)")
    public void setFlWheelSpd(Float flWheelSpd) {
        this.flWheelSpd = flWheelSpd;
    }

    @JsonProperty(value="FRWheelSpd( Km/h)")
    public void setFrWheelSpd(Float frWheelSpd) {
        this.frWheelSpd = frWheelSpd;
    }

    @JsonProperty(value="ESC_Mcylinder_Pressure( bar)")
    public void setEscMcylinderPressure(Float escMcylinderPressure) {
        this.escMcylinderPressure = escMcylinderPressure;
    }

    @JsonProperty(value="RLWheelSpd( Km/h)")
    public void setRlWheelSpd(Float rlWheelSpd) {
        this.rlWheelSpd = rlWheelSpd;
    }

    @JsonProperty(value="RRWheelSpd( Km/h)")
    public void setRrWheelSpd(Float rrWheelSpd) {
        this.rrWheelSpd = rrWheelSpd;
    }

    @JsonProperty(value="LongitudeAcc( m/s^2)")
    public void setLongitudeAcc(Float longitudeAcc) {
        this.longitudeAcc = longitudeAcc;
    }

    @JsonProperty(value="received_at")
    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    public void setDeltaTime(Float deltaTime) {
        this.deltaTime = deltaTime;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public void setLeftSpeedDiff(Float leftSpeedDiff) {
        this.leftSpeedDiff = leftSpeedDiff;
    }

    public void setRightSpeedDiff(Float rightSpeedDiff) {
        this.rightSpeedDiff = rightSpeedDiff;
    }

    public void setLeftSteeringRatio(Float leftSteeringRatio) {
        this.leftSteeringRatio = leftSteeringRatio;
    }

    public void setRightSteeringRatio(Float rightSteeringRatio) {
        this.rightSteeringRatio = rightSteeringRatio;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Frame)) {
            return false;
        }
        Frame other = (Frame)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Float this$vehicleSpd = this.getVehicleSpd();
        Float other$vehicleSpd = other.getVehicleSpd();
        if (this$vehicleSpd == null ? other$vehicleSpd != null : !((Object)this$vehicleSpd).equals(other$vehicleSpd)) {
            return false;
        }
        Float this$steerWheelAngle = this.getSteerWheelAngle();
        Float other$steerWheelAngle = other.getSteerWheelAngle();
        if (this$steerWheelAngle == null ? other$steerWheelAngle != null : !((Object)this$steerWheelAngle).equals(other$steerWheelAngle)) {
            return false;
        }
        Float this$lateralAcce = this.getLateralAcce();
        Float other$lateralAcce = other.getLateralAcce();
        if (this$lateralAcce == null ? other$lateralAcce != null : !((Object)this$lateralAcce).equals(other$lateralAcce)) {
            return false;
        }
        Float this$flWheelSpd = this.getFlWheelSpd();
        Float other$flWheelSpd = other.getFlWheelSpd();
        if (this$flWheelSpd == null ? other$flWheelSpd != null : !((Object)this$flWheelSpd).equals(other$flWheelSpd)) {
            return false;
        }
        Float this$frWheelSpd = this.getFrWheelSpd();
        Float other$frWheelSpd = other.getFrWheelSpd();
        if (this$frWheelSpd == null ? other$frWheelSpd != null : !((Object)this$frWheelSpd).equals(other$frWheelSpd)) {
            return false;
        }
        Float this$escMcylinderPressure = this.getEscMcylinderPressure();
        Float other$escMcylinderPressure = other.getEscMcylinderPressure();
        if (this$escMcylinderPressure == null ? other$escMcylinderPressure != null : !((Object)this$escMcylinderPressure).equals(other$escMcylinderPressure)) {
            return false;
        }
        Float this$rlWheelSpd = this.getRlWheelSpd();
        Float other$rlWheelSpd = other.getRlWheelSpd();
        if (this$rlWheelSpd == null ? other$rlWheelSpd != null : !((Object)this$rlWheelSpd).equals(other$rlWheelSpd)) {
            return false;
        }
        Float this$rrWheelSpd = this.getRrWheelSpd();
        Float other$rrWheelSpd = other.getRrWheelSpd();
        if (this$rrWheelSpd == null ? other$rrWheelSpd != null : !((Object)this$rrWheelSpd).equals(other$rrWheelSpd)) {
            return false;
        }
        Float this$longitudeAcc = this.getLongitudeAcc();
        Float other$longitudeAcc = other.getLongitudeAcc();
        if (this$longitudeAcc == null ? other$longitudeAcc != null : !((Object)this$longitudeAcc).equals(other$longitudeAcc)) {
            return false;
        }
        Float this$deltaTime = this.getDeltaTime();
        Float other$deltaTime = other.getDeltaTime();
        if (this$deltaTime == null ? other$deltaTime != null : !((Object)this$deltaTime).equals(other$deltaTime)) {
            return false;
        }
        Float this$distance = this.getDistance();
        Float other$distance = other.getDistance();
        if (this$distance == null ? other$distance != null : !((Object)this$distance).equals(other$distance)) {
            return false;
        }
        Float this$leftSpeedDiff = this.getLeftSpeedDiff();
        Float other$leftSpeedDiff = other.getLeftSpeedDiff();
        if (this$leftSpeedDiff == null ? other$leftSpeedDiff != null : !((Object)this$leftSpeedDiff).equals(other$leftSpeedDiff)) {
            return false;
        }
        Float this$rightSpeedDiff = this.getRightSpeedDiff();
        Float other$rightSpeedDiff = other.getRightSpeedDiff();
        if (this$rightSpeedDiff == null ? other$rightSpeedDiff != null : !((Object)this$rightSpeedDiff).equals(other$rightSpeedDiff)) {
            return false;
        }
        Float this$leftSteeringRatio = this.getLeftSteeringRatio();
        Float other$leftSteeringRatio = other.getLeftSteeringRatio();
        if (this$leftSteeringRatio == null ? other$leftSteeringRatio != null : !((Object)this$leftSteeringRatio).equals(other$leftSteeringRatio)) {
            return false;
        }
        Float this$rightSteeringRatio = this.getRightSteeringRatio();
        Float other$rightSteeringRatio = other.getRightSteeringRatio();
        if (this$rightSteeringRatio == null ? other$rightSteeringRatio != null : !((Object)this$rightSteeringRatio).equals(other$rightSteeringRatio)) {
            return false;
        }
        String this$date = this.getDate();
        String other$date = other.getDate();
        if (this$date == null ? other$date != null : !this$date.equals(other$date)) {
            return false;
        }
        String this$sn = this.getSn();
        String other$sn = other.getSn();
        if (this$sn == null ? other$sn != null : !this$sn.equals(other$sn)) {
            return false;
        }
        String this$wiperFlag = this.getWiperFlag();
        String other$wiperFlag = other.getWiperFlag();
        if (this$wiperFlag == null ? other$wiperFlag != null : !this$wiperFlag.equals(other$wiperFlag)) {
            return false;
        }
        String this$receivedTime = this.getReceivedTime();
        String other$receivedTime = other.getReceivedTime();
        return !(this$receivedTime == null ? other$receivedTime != null : !this$receivedTime.equals(other$receivedTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Frame;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Float $vehicleSpd = this.getVehicleSpd();
        result = result * 59 + ($vehicleSpd == null ? 43 : ((Object)$vehicleSpd).hashCode());
        Float $steerWheelAngle = this.getSteerWheelAngle();
        result = result * 59 + ($steerWheelAngle == null ? 43 : ((Object)$steerWheelAngle).hashCode());
        Float $lateralAcce = this.getLateralAcce();
        result = result * 59 + ($lateralAcce == null ? 43 : ((Object)$lateralAcce).hashCode());
        Float $flWheelSpd = this.getFlWheelSpd();
        result = result * 59 + ($flWheelSpd == null ? 43 : ((Object)$flWheelSpd).hashCode());
        Float $frWheelSpd = this.getFrWheelSpd();
        result = result * 59 + ($frWheelSpd == null ? 43 : ((Object)$frWheelSpd).hashCode());
        Float $escMcylinderPressure = this.getEscMcylinderPressure();
        result = result * 59 + ($escMcylinderPressure == null ? 43 : ((Object)$escMcylinderPressure).hashCode());
        Float $rlWheelSpd = this.getRlWheelSpd();
        result = result * 59 + ($rlWheelSpd == null ? 43 : ((Object)$rlWheelSpd).hashCode());
        Float $rrWheelSpd = this.getRrWheelSpd();
        result = result * 59 + ($rrWheelSpd == null ? 43 : ((Object)$rrWheelSpd).hashCode());
        Float $longitudeAcc = this.getLongitudeAcc();
        result = result * 59 + ($longitudeAcc == null ? 43 : ((Object)$longitudeAcc).hashCode());
        Float $deltaTime = this.getDeltaTime();
        result = result * 59 + ($deltaTime == null ? 43 : ((Object)$deltaTime).hashCode());
        Float $distance = this.getDistance();
        result = result * 59 + ($distance == null ? 43 : ((Object)$distance).hashCode());
        Float $leftSpeedDiff = this.getLeftSpeedDiff();
        result = result * 59 + ($leftSpeedDiff == null ? 43 : ((Object)$leftSpeedDiff).hashCode());
        Float $rightSpeedDiff = this.getRightSpeedDiff();
        result = result * 59 + ($rightSpeedDiff == null ? 43 : ((Object)$rightSpeedDiff).hashCode());
        Float $leftSteeringRatio = this.getLeftSteeringRatio();
        result = result * 59 + ($leftSteeringRatio == null ? 43 : ((Object)$leftSteeringRatio).hashCode());
        Float $rightSteeringRatio = this.getRightSteeringRatio();
        result = result * 59 + ($rightSteeringRatio == null ? 43 : ((Object)$rightSteeringRatio).hashCode());
        String $date = this.getDate();
        result = result * 59 + ($date == null ? 43 : $date.hashCode());
        String $sn = this.getSn();
        result = result * 59 + ($sn == null ? 43 : $sn.hashCode());
        String $wiperFlag = this.getWiperFlag();
        result = result * 59 + ($wiperFlag == null ? 43 : $wiperFlag.hashCode());
        String $receivedTime = this.getReceivedTime();
        result = result * 59 + ($receivedTime == null ? 43 : $receivedTime.hashCode());
        return result;
    }

    public String toString() {
        return "Frame(date=" + this.getDate() + ", sn=" + this.getSn() + ", vehicleSpd=" + this.getVehicleSpd() + ", wiperFlag=" + this.getWiperFlag() + ", steerWheelAngle=" + this.getSteerWheelAngle() + ", lateralAcce=" + this.getLateralAcce() + ", flWheelSpd=" + this.getFlWheelSpd() + ", frWheelSpd=" + this.getFrWheelSpd() + ", escMcylinderPressure=" + this.getEscMcylinderPressure() + ", rlWheelSpd=" + this.getRlWheelSpd() + ", rrWheelSpd=" + this.getRrWheelSpd() + ", longitudeAcc=" + this.getLongitudeAcc() + ", receivedTime=" + this.getReceivedTime() + ", deltaTime=" + this.getDeltaTime() + ", distance=" + this.getDistance() + ", leftSpeedDiff=" + this.getLeftSpeedDiff() + ", rightSpeedDiff=" + this.getRightSpeedDiff() + ", leftSteeringRatio=" + this.getLeftSteeringRatio() + ", rightSteeringRatio=" + this.getRightSteeringRatio() + ")";
    }
}

