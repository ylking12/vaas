/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.dto.AddCarMappingRequest
 *  com.etas.vaas.admin.dto.UpdateCarMappingRequest
 *  jakarta.validation.constraints.NotNull
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;

import com.etas.vaas.admin.dto.AddCarMappingRequest;
import jakarta.validation.constraints.NotNull;

public class UpdateCarMappingRequest
extends AddCarMappingRequest {
    @NotNull(message="id\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="id\u4e0d\u80fd\u4e3a\u7a7a") Long id;

    public UpdateCarMappingRequest() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toString() {
        return "UpdateCarMappingRequest(id=" + this.getId() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UpdateCarMappingRequest)) {
            return false;
        }
        UpdateCarMappingRequest other = (UpdateCarMappingRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        return !(this$id == null ? other$id != null : !((Object)this$id).equals(other$id));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UpdateCarMappingRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = super.hashCode();
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        return result;
    }
}

