/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto;

import com.etas.vaas.common.dao.VehicleEventDao;

public class TempVehicleEvent
extends VehicleEventDao {
    private Integer status;
    private String sn;

    public TempVehicleEvent() {
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getSn() {
        return this.sn;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "TempVehicleEvent(status=" + this.getStatus() + ", sn=" + this.getSn() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TempVehicleEvent)) {
            return false;
        }
        TempVehicleEvent other = (TempVehicleEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Integer this$status = this.getStatus();
        Integer other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)this$status).equals(other$status)) {
            return false;
        }
        String this$sn = this.getSn();
        String other$sn = other.getSn();
        return !(this$sn == null ? other$sn != null : !this$sn.equals(other$sn));
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof TempVehicleEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Integer $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)$status).hashCode());
        String $sn = this.getSn();
        result = result * 59 + ($sn == null ? 43 : $sn.hashCode());
        return result;
    }
}

