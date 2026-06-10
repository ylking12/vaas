/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.dto.GetCarMappingListResp
 *  com.etas.vaas.admin.dto.GetCarMappingListResp$EachFleetManagement
 *  com.etas.vaas.admin.dto.Pagination
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;

import com.etas.vaas.admin.dto.GetCarMappingListResp;
import com.etas.vaas.admin.dto.Pagination;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@SuppressWarnings("unused")
public class GetCarMappingListResp {
    @JsonProperty(value="list")
    private List<EachFleetManagement> fleetManagementList;
    private Pagination pagination;

    public GetCarMappingListResp() {
    }

    public List<EachFleetManagement> getFleetManagementList() {
        return this.fleetManagementList;
    }

    public Pagination getPagination() {
        return this.pagination;
    }

    @JsonProperty(value="list")
    public void setFleetManagementList(List<EachFleetManagement> fleetManagementList) {
        this.fleetManagementList = fleetManagementList;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GetCarMappingListResp)) {
            return false;
        }
        GetCarMappingListResp other = (GetCarMappingListResp)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        List this$fleetManagementList = this.getFleetManagementList();
        List other$fleetManagementList = other.getFleetManagementList();
        if (this$fleetManagementList == null ? other$fleetManagementList != null : !((Object)this$fleetManagementList).equals(other$fleetManagementList)) {
            return false;
        }
        Pagination this$pagination = this.getPagination();
        Pagination other$pagination = other.getPagination();
        return !(this$pagination == null ? other$pagination != null : !this$pagination.equals(other$pagination));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GetCarMappingListResp;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List $fleetManagementList = this.getFleetManagementList();
        result = result * 59 + ($fleetManagementList == null ? 43 : ((Object)$fleetManagementList).hashCode());
        Pagination $pagination = this.getPagination();
        result = result * 59 + ($pagination == null ? 43 : $pagination.hashCode());
        return result;
    }

    public String toString() {
        return "GetCarMappingListResp(fleetManagementList=" + this.getFleetManagementList() + ", pagination=" + this.getPagination() + ")";
    }

    public static class EachFleetManagement {
        private Long no;
        private Long id;
        private String imei;
        private String kt710Id;
        private String plate;
        private String brandModel;
        private Boolean bumpEnable;
        private Boolean slipEnable;
        private Boolean reject;
        private String simId;
        private Integer groupId;
        private Long updateAt;
        public Long getNo() { return no; }
        public void setNo(Long v) { this.no = v; }
        public Long getId() { return id; }
        public void setId(Long v) { this.id = v; }
        public String getImei() { return imei; }
        public void setImei(String v) { this.imei = v; }
        public String getKt710Id() { return kt710Id; }
        public void setKt710Id(String v) { this.kt710Id = v; }
        public String getPlate() { return plate; }
        public void setPlate(String v) { this.plate = v; }
        public String getBrandModel() { return brandModel; }
        public void setBrandModel(String v) { this.brandModel = v; }
        public Boolean getBumpEnable() { return bumpEnable; }
        public void setBumpEnable(Boolean v) { this.bumpEnable = v; }
        public Boolean getSlipEnable() { return slipEnable; }
        public void setSlipEnable(Boolean v) { this.slipEnable = v; }
        public Boolean getReject() { return reject; }
        public void setReject(Boolean v) { this.reject = v; }
        public String getSimId() { return simId; }
        public void setSimId(String v) { this.simId = v; }
        public Integer getGroupId() { return groupId; }
        public void setGroupId(Integer v) { this.groupId = v; }
        public Long getUpdateAt() { return updateAt; }
        public void setUpdateAt(Long v) { this.updateAt = v; }
    }
}

