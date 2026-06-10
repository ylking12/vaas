/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto;

import com.etas.vaas.common.dao.VehicleEventDao;

public class KtVehicleEvent
extends VehicleEventDao {
    private String sn;

    public KtVehicleEvent() {
    }

    public String getSn() {
        return this.sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "KtVehicleEvent(super=" + super.toString() + ", sn=" + this.getSn() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof KtVehicleEvent)) {
            return false;
        }
        KtVehicleEvent other = (KtVehicleEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        String this$sn = this.getSn();
        String other$sn = other.getSn();
        return !(this$sn == null ? other$sn != null : !this$sn.equals(other$sn));
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof KtVehicleEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        String $sn = this.getSn();
        result = result * 59 + ($sn == null ? 43 : $sn.hashCode());
        return result;
    }
}

