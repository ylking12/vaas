/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.vo.RearMirrorResp
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.backend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RearMirrorResp {
    @JsonProperty(value="event_type")
    private String eventType;
    @JsonProperty(value="lon")
    private double longitude;
    @JsonProperty(value="lat")
    private double latitude;

    public RearMirrorResp() {
    }

    public String getEventType() {
        return this.eventType;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    @JsonProperty(value="event_type")
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @JsonProperty(value="lon")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @JsonProperty(value="lat")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RearMirrorResp)) {
            return false;
        }
        RearMirrorResp other = (RearMirrorResp)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getLongitude(), other.getLongitude()) != 0) {
            return false;
        }
        if (Double.compare(this.getLatitude(), other.getLatitude()) != 0) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        return !(this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RearMirrorResp;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $longitude = Double.doubleToLongBits(this.getLongitude());
        result = result * 59 + (int)($longitude >>> 32 ^ $longitude);
        long $latitude = Double.doubleToLongBits(this.getLatitude());
        result = result * 59 + (int)($latitude >>> 32 ^ $latitude);
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        return result;
    }

    public String toString() {
        return "RearMirrorResp(eventType=" + this.getEventType() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ")";
    }
}

