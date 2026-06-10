/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.dto.LicensePlate
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;


public class LicensePlate {
    public LicensePlate() {}

    private String plateString;
    private boolean success;

    public String getPlateString() {
        return this.plateString;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setPlateString(String plateString) {
        this.plateString = plateString;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LicensePlate)) {
            return false;
        }
        LicensePlate other = (LicensePlate)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        String this$plateString = this.getPlateString();
        String other$plateString = other.getPlateString();
        return !(this$plateString == null ? other$plateString != null : !this$plateString.equals(other$plateString));
    }

    protected boolean canEqual(Object other) {
        return other instanceof LicensePlate;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        String $plateString = this.getPlateString();
        result = result * 59 + ($plateString == null ? 43 : $plateString.hashCode());
        return result;
    }

    public String toString() {
        return "LicensePlate(plateString=" + this.getPlateString() + ", success=" + this.isSuccess() + ")";
    }

    public LicensePlate(String plateString, boolean success) {
        this.plateString = plateString;
        this.success = success;
    }
}

