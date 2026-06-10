/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.etas.vaas.receiver.entity.FleetManagement
 */
package com.etas.vaas.receiver.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value="fleet_management")
public class FleetManagement {
    @TableId
    private Long id;
    private String imei;
    private String kt710Id;
    private String plate;
    private String dataType;
    private Integer groupId;

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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FleetManagement)) {
            return false;
        }
        FleetManagement other = (FleetManagement)o;
        if (!other.canEqual((Object)this)) {
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
        return !(this$dataType == null ? other$dataType != null : !this$dataType.equals(other$dataType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof FleetManagement;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $groupId = this.getGroupId();
        result = result * 59 + ($groupId == null ? 43 : ((Object)$groupId).hashCode());
        String $imei = this.getImei();
        result = result * 59 + ($imei == null ? 43 : $imei.hashCode());
        String $kt710Id = this.getKt710Id();
        result = result * 59 + ($kt710Id == null ? 43 : $kt710Id.hashCode());
        String $plate = this.getPlate();
        result = result * 59 + ($plate == null ? 43 : $plate.hashCode());
        String $dataType = this.getDataType();
        result = result * 59 + ($dataType == null ? 43 : $dataType.hashCode());
        return result;
    }

    public String toString() {
        return "FleetManagement(id=" + this.getId() + ", imei=" + this.getImei() + ", kt710Id=" + this.getKt710Id() + ", plate=" + this.getPlate() + ", dataType=" + this.getDataType() + ", groupId=" + this.getGroupId() + ")";
    }
}

