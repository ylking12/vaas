/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.IdType
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 */
package com.etas.vaas.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value="brand_model")
public class BrandModel {
    @TableId(type=IdType.AUTO)
    private Integer modelId;
    private String modelName;
    private String modelValue;

    public BrandModel() {
    }

    public Integer getModelId() {
        return this.modelId;
    }

    public String getModelName() {
        return this.modelName;
    }

    public String getModelValue() {
        return this.modelValue;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setModelValue(String modelValue) {
        this.modelValue = modelValue;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BrandModel)) {
            return false;
        }
        BrandModel other = (BrandModel)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$modelId = this.getModelId();
        Integer other$modelId = other.getModelId();
        if (this$modelId == null ? other$modelId != null : !((Object)this$modelId).equals(other$modelId)) {
            return false;
        }
        String this$modelName = this.getModelName();
        String other$modelName = other.getModelName();
        if (this$modelName == null ? other$modelName != null : !this$modelName.equals(other$modelName)) {
            return false;
        }
        String this$modelValue = this.getModelValue();
        String other$modelValue = other.getModelValue();
        return !(this$modelValue == null ? other$modelValue != null : !this$modelValue.equals(other$modelValue));
    }

    protected boolean canEqual(Object other) {
        return other instanceof BrandModel;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $modelId = this.getModelId();
        result = result * 59 + ($modelId == null ? 43 : ((Object)$modelId).hashCode());
        String $modelName = this.getModelName();
        result = result * 59 + ($modelName == null ? 43 : $modelName.hashCode());
        String $modelValue = this.getModelValue();
        result = result * 59 + ($modelValue == null ? 43 : $modelValue.hashCode());
        return result;
    }

    public String toString() {
        return "BrandModel(modelId=" + this.getModelId() + ", modelName=" + this.getModelName() + ", modelValue=" + this.getModelValue() + ")";
    }
}

