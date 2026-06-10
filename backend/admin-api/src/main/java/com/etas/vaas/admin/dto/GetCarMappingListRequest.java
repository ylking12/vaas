/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.dto.GetCarMappingListRequest
 *  com.etas.vaas.admin.dto.Pagination
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;

import com.etas.vaas.admin.dto.Pagination;

public class GetCarMappingListRequest {
    private String deviceId;
    private String kt710Id;
    private String plate;
    private Integer groupId;
    private String simId;
    private Boolean reject;
    private Boolean bumpEnable;
    private Boolean slipEnable;
    private String brandModel;
    private Pagination pagination;

    public GetCarMappingListRequest() {
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getKt710Id() {
        return this.kt710Id;
    }

    public String getPlate() {
        return this.plate;
    }

    public Integer getGroupId() {
        return this.groupId;
    }

    public String getSimId() {
        return this.simId;
    }

    public Boolean getReject() {
        return this.reject;
    }

    public Boolean getBumpEnable() {
        return this.bumpEnable;
    }

    public Boolean getSlipEnable() {
        return this.slipEnable;
    }

    public String getBrandModel() {
        return this.brandModel;
    }

    public Pagination getPagination() {
        return this.pagination;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setKt710Id(String kt710Id) {
        this.kt710Id = kt710Id;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public void setReject(Boolean reject) {
        this.reject = reject;
    }

    public void setBumpEnable(Boolean bumpEnable) {
        this.bumpEnable = bumpEnable;
    }

    public void setSlipEnable(Boolean slipEnable) {
        this.slipEnable = slipEnable;
    }

    public void setBrandModel(String brandModel) {
        this.brandModel = brandModel;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GetCarMappingListRequest)) {
            return false;
        }
        GetCarMappingListRequest other = (GetCarMappingListRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$groupId = this.getGroupId();
        Integer other$groupId = other.getGroupId();
        if (this$groupId == null ? other$groupId != null : !((Object)this$groupId).equals(other$groupId)) {
            return false;
        }
        Boolean this$reject = this.getReject();
        Boolean other$reject = other.getReject();
        if (this$reject == null ? other$reject != null : !((Object)this$reject).equals(other$reject)) {
            return false;
        }
        Boolean this$bumpEnable = this.getBumpEnable();
        Boolean other$bumpEnable = other.getBumpEnable();
        if (this$bumpEnable == null ? other$bumpEnable != null : !((Object)this$bumpEnable).equals(other$bumpEnable)) {
            return false;
        }
        Boolean this$slipEnable = this.getSlipEnable();
        Boolean other$slipEnable = other.getSlipEnable();
        if (this$slipEnable == null ? other$slipEnable != null : !((Object)this$slipEnable).equals(other$slipEnable)) {
            return false;
        }
        String this$deviceId = this.getDeviceId();
        String other$deviceId = other.getDeviceId();
        if (this$deviceId == null ? other$deviceId != null : !this$deviceId.equals(other$deviceId)) {
            return false;
        }
        String this$kt710Id = this.getKt710Id();
        String other$kt710Id = other.getKt710Id();
        if (this$kt710Id == null ? other$kt710Id != null : !this$kt710Id.equals(other$kt710Id)) {
            return false;
        }
        String this$plate = this.getPlate();
        String other$plate = other.getPlate();
        if (this$plate == null ? other$plate != null : !this$plate.equals(other$plate)) {
            return false;
        }
        String this$simId = this.getSimId();
        String other$simId = other.getSimId();
        if (this$simId == null ? other$simId != null : !this$simId.equals(other$simId)) {
            return false;
        }
        String this$brandModel = this.getBrandModel();
        String other$brandModel = other.getBrandModel();
        if (this$brandModel == null ? other$brandModel != null : !this$brandModel.equals(other$brandModel)) {
            return false;
        }
        Pagination this$pagination = this.getPagination();
        Pagination other$pagination = other.getPagination();
        return !(this$pagination == null ? other$pagination != null : !this$pagination.equals(other$pagination));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GetCarMappingListRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $groupId = this.getGroupId();
        result = result * 59 + ($groupId == null ? 43 : ((Object)$groupId).hashCode());
        Boolean $reject = this.getReject();
        result = result * 59 + ($reject == null ? 43 : ((Object)$reject).hashCode());
        Boolean $bumpEnable = this.getBumpEnable();
        result = result * 59 + ($bumpEnable == null ? 43 : ((Object)$bumpEnable).hashCode());
        Boolean $slipEnable = this.getSlipEnable();
        result = result * 59 + ($slipEnable == null ? 43 : ((Object)$slipEnable).hashCode());
        String $deviceId = this.getDeviceId();
        result = result * 59 + ($deviceId == null ? 43 : $deviceId.hashCode());
        String $kt710Id = this.getKt710Id();
        result = result * 59 + ($kt710Id == null ? 43 : $kt710Id.hashCode());
        String $plate = this.getPlate();
        result = result * 59 + ($plate == null ? 43 : $plate.hashCode());
        String $simId = this.getSimId();
        result = result * 59 + ($simId == null ? 43 : $simId.hashCode());
        String $brandModel = this.getBrandModel();
        result = result * 59 + ($brandModel == null ? 43 : $brandModel.hashCode());
        Pagination $pagination = this.getPagination();
        result = result * 59 + ($pagination == null ? 43 : $pagination.hashCode());
        return result;
    }

    public String toString() {
        return "GetCarMappingListRequest(deviceId=" + this.getDeviceId() + ", kt710Id=" + this.getKt710Id() + ", plate=" + this.getPlate() + ", groupId=" + this.getGroupId() + ", simId=" + this.getSimId() + ", reject=" + this.getReject() + ", bumpEnable=" + this.getBumpEnable() + ", slipEnable=" + this.getSlipEnable() + ", brandModel=" + this.getBrandModel() + ", pagination=" + this.getPagination() + ")";
    }
}

