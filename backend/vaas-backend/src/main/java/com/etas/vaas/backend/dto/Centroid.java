/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.Centroid
 */
package com.etas.vaas.backend.dto;


public class Centroid {
    private String name;
    private double longitude;
    private double latitude;

    public Centroid() {
    }

    public String getName() {
        return this.name;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Centroid)) {
            return false;
        }
        Centroid other = (Centroid)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Double.compare(this.getLongitude(), other.getLongitude()) != 0) {
            return false;
        }
        if (Double.compare(this.getLatitude(), other.getLatitude()) != 0) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        return !(this$name == null ? other$name != null : !this$name.equals(other$name));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Centroid;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $longitude = Double.doubleToLongBits(this.getLongitude());
        result = result * 59 + (int)($longitude >>> 32 ^ $longitude);
        long $latitude = Double.doubleToLongBits(this.getLatitude());
        result = result * 59 + (int)($latitude >>> 32 ^ $latitude);
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public String toString() {
        return "Centroid(name=" + this.getName() + ", longitude=" + this.getLongitude() + ", latitude=" + this.getLatitude() + ")";
    }
}

