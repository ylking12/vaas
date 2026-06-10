/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.vo.PlotResp
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.backend.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlotResp {
    @JsonProperty(value="time")
    private String time;
    @JsonProperty(value="value")
    private Float value;

    public PlotResp(String time, float value) {
        this.time = time;
        this.value = Float.valueOf(value);
    }

    public String getTime() {
        return this.time;
    }

    public Float getValue() {
        return this.value;
    }

    @JsonProperty(value="time")
    public void setTime(String time) {
        this.time = time;
    }

    @JsonProperty(value="value")
    public void setValue(Float value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlotResp)) {
            return false;
        }
        PlotResp other = (PlotResp)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Float this$value = this.getValue();
        Float other$value = other.getValue();
        if (this$value == null ? other$value != null : !((Object)this$value).equals(other$value)) {
            return false;
        }
        String this$time = this.getTime();
        String other$time = other.getTime();
        return !(this$time == null ? other$time != null : !this$time.equals(other$time));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlotResp;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Float $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : ((Object)$value).hashCode());
        String $time = this.getTime();
        result = result * 59 + ($time == null ? 43 : $time.hashCode());
        return result;
    }

    public String toString() {
        return "PlotResp(time=" + this.getTime() + ", value=" + this.getValue() + ")";
    }

    public PlotResp() {
    }
}

