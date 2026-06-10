/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.dto.AddCarMappingRequest
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.NotNull
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AddCarMappingRequest {
    @NotBlank(message="\u8bbe\u5907\u53f7\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8bbe\u5907\u53f7\u4e0d\u80fd\u4e3a\u7a7a") String deviceId;
    @NotBlank(message="kt710 SN\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="kt710 SN\u4e0d\u80fd\u4e3a\u7a7a") String kt710Id;
    @NotBlank(message="\u8f66\u724c\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8f66\u724c\u4e0d\u80fd\u4e3a\u7a7a") String plate;
    @NotNull(message="\u7ec4\u53f7\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u7ec4\u53f7\u4e0d\u80fd\u4e3a\u7a7a") Integer groupId;
    @NotNull(message="\u98a0\u7c38\u7b97\u6cd5\u4f7f\u80fd\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u98a0\u7c38\u7b97\u6cd5\u4f7f\u80fd\u4e0d\u80fd\u4e3a\u7a7a") boolean bumpEnable;
    @NotNull(message="\u6e7f\u6ed1\u7b97\u6cd5\u4f7f\u80fd\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u6e7f\u6ed1\u7b97\u6cd5\u4f7f\u80fd\u4e0d\u80fd\u4e3a\u7a7a") boolean slipEnable;
    @NotNull(message="\u62d2\u6536\u6807\u5fd7\u4f4d\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u62d2\u6536\u6807\u5fd7\u4f4d\u4e0d\u80fd\u4e3a\u7a7a") boolean reject;
    @NotBlank(message="SIM\u5361\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="SIM\u5361\u4e0d\u80fd\u4e3a\u7a7a") String simId;
    @NotBlank(message="\u8f66\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u8f66\u578b\u4e0d\u80fd\u4e3a\u7a7a") String brandModel;

    public AddCarMappingRequest() {
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

    public boolean isBumpEnable() {
        return this.bumpEnable;
    }

    public boolean isSlipEnable() {
        return this.slipEnable;
    }

    public boolean isReject() {
        return this.reject;
    }

    public String getSimId() {
        return this.simId;
    }

    public String getBrandModel() {
        return this.brandModel;
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

    public void setBumpEnable(boolean bumpEnable) {
        this.bumpEnable = bumpEnable;
    }

    public void setSlipEnable(boolean slipEnable) {
        this.slipEnable = slipEnable;
    }

    public void setReject(boolean reject) {
        this.reject = reject;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public void setBrandModel(String brandModel) {
        this.brandModel = brandModel;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AddCarMappingRequest)) {
            return false;
        }
        AddCarMappingRequest other = (AddCarMappingRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isBumpEnable() != other.isBumpEnable()) {
            return false;
        }
        if (this.isSlipEnable() != other.isSlipEnable()) {
            return false;
        }
        if (this.isReject() != other.isReject()) {
            return false;
        }
        Integer this$groupId = this.getGroupId();
        Integer other$groupId = other.getGroupId();
        if (this$groupId == null ? other$groupId != null : !((Object)this$groupId).equals(other$groupId)) {
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
        return !(this$brandModel == null ? other$brandModel != null : !this$brandModel.equals(other$brandModel));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AddCarMappingRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isBumpEnable() ? 79 : 97);
        result = result * 59 + (this.isSlipEnable() ? 79 : 97);
        result = result * 59 + (this.isReject() ? 79 : 97);
        Integer $groupId = this.getGroupId();
        result = result * 59 + ($groupId == null ? 43 : ((Object)$groupId).hashCode());
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
        return result;
    }

    public String toString() {
        return "AddCarMappingRequest(deviceId=" + this.getDeviceId() + ", kt710Id=" + this.getKt710Id() + ", plate=" + this.getPlate() + ", groupId=" + this.getGroupId() + ", bumpEnable=" + this.isBumpEnable() + ", slipEnable=" + this.isSlipEnable() + ", reject=" + this.isReject() + ", simId=" + this.getSimId() + ", brandModel=" + this.getBrandModel() + ")";
    }
}

