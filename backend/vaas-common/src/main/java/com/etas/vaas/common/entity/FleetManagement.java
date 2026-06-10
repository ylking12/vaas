/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.FieldFill
 *  com.baomidou.mybatisplus.annotation.IdType
 *  com.baomidou.mybatisplus.annotation.TableField
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  jakarta.validation.constraints.Digits
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.NotNull
 *  jakarta.validation.constraints.Pattern
 */
package com.etas.vaas.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@TableName(value="fleet_management")
public class FleetManagement {
    @TableId(type=IdType.AUTO)
    @NotNull
    private Long id;
    @NotBlank
    private String imei;
    @NotBlank
    private String kt710Id;
    @NotBlank
    private String plate;
    @NotBlank
    @Pattern(regexp="^(kt710|6a)$")
    private @NotBlank @Pattern(regexp="^(kt710|6a)$") String dataType;
    @NotNull
    @Digits(integer=3, fraction=0)
    private @NotNull @Digits(integer=3, fraction=0) Integer groupId;
    private boolean bumpEnable;
    private boolean slipEnable;
    private String simId;
    private String brandModel;
    private boolean reject;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private Long updateAt;
    private String phoneNumber;

    public FleetManagement() {
    }

    public Long getId() {
        return this.id;
    }

    public String getImei() {
        return this.imei;
    }

    public String getKt710Id() {
        return this.kt710Id;
    }

    public String getPlate() {
        return this.plate;
    }

    public String getDataType() {
        return this.dataType;
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

    public String getSimId() {
        return this.simId;
    }

    public String getBrandModel() {
        return this.brandModel;
    }

    public boolean isReject() {
        return this.reject;
    }

    public Long getUpdateAt() {
        return this.updateAt;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setKt710Id(String kt710Id) {
        this.kt710Id = kt710Id;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public void setBrandModel(String brandModel) {
        this.brandModel = brandModel;
    }

    public void setReject(boolean reject) {
        this.reject = reject;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FleetManagement)) {
            return false;
        }
        FleetManagement other = (FleetManagement)o;
        if (!other.canEqual(this)) {
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
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$groupId = this.getGroupId();
        Integer other$groupId = other.getGroupId();
        if (this$groupId == null ? other$groupId != null : !((Object)this$groupId).equals(other$groupId)) {
            return false;
        }
        Long this$updateAt = this.getUpdateAt();
        Long other$updateAt = other.getUpdateAt();
        if (this$updateAt == null ? other$updateAt != null : !((Object)this$updateAt).equals(other$updateAt)) {
            return false;
        }
        String this$imei = this.getImei();
        String other$imei = other.getImei();
        if (this$imei == null ? other$imei != null : !this$imei.equals(other$imei)) {
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
        String this$dataType = this.getDataType();
        String other$dataType = other.getDataType();
        if (this$dataType == null ? other$dataType != null : !this$dataType.equals(other$dataType)) {
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
        String this$phoneNumber = this.getPhoneNumber();
        String other$phoneNumber = other.getPhoneNumber();
        return !(this$phoneNumber == null ? other$phoneNumber != null : !this$phoneNumber.equals(other$phoneNumber));
    }

    protected boolean canEqual(Object other) {
        return other instanceof FleetManagement;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isBumpEnable() ? 79 : 97);
        result = result * 59 + (this.isSlipEnable() ? 79 : 97);
        result = result * 59 + (this.isReject() ? 79 : 97);
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $groupId = this.getGroupId();
        result = result * 59 + ($groupId == null ? 43 : ((Object)$groupId).hashCode());
        Long $updateAt = this.getUpdateAt();
        result = result * 59 + ($updateAt == null ? 43 : ((Object)$updateAt).hashCode());
        String $imei = this.getImei();
        result = result * 59 + ($imei == null ? 43 : $imei.hashCode());
        String $kt710Id = this.getKt710Id();
        result = result * 59 + ($kt710Id == null ? 43 : $kt710Id.hashCode());
        String $plate = this.getPlate();
        result = result * 59 + ($plate == null ? 43 : $plate.hashCode());
        String $dataType = this.getDataType();
        result = result * 59 + ($dataType == null ? 43 : $dataType.hashCode());
        String $simId = this.getSimId();
        result = result * 59 + ($simId == null ? 43 : $simId.hashCode());
        String $brandModel = this.getBrandModel();
        result = result * 59 + ($brandModel == null ? 43 : $brandModel.hashCode());
        String $phoneNumber = this.getPhoneNumber();
        result = result * 59 + ($phoneNumber == null ? 43 : $phoneNumber.hashCode());
        return result;
    }

    public String toString() {
        return "FleetManagement(id=" + this.getId() + ", imei=" + this.getImei() + ", kt710Id=" + this.getKt710Id() + ", plate=" + this.getPlate() + ", dataType=" + this.getDataType() + ", groupId=" + this.getGroupId() + ", bumpEnable=" + this.isBumpEnable() + ", slipEnable=" + this.isSlipEnable() + ", simId=" + this.getSimId() + ", brandModel=" + this.getBrandModel() + ", reject=" + this.isReject() + ", updateAt=" + this.getUpdateAt() + ", phoneNumber=" + this.getPhoneNumber() + ")";
    }
}

