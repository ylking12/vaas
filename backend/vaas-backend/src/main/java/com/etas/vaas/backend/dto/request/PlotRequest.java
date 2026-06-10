/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.request.PlotRequest
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlotRequest {
    @JsonProperty(value="data_title")
    private String dataTitle;
    @JsonProperty(value="road_name")
    private String roadName;

    public PlotRequest() {
    }

    public String getDataTitle() {
        return this.dataTitle;
    }

    public String getRoadName() {
        return this.roadName;
    }

    @JsonProperty(value="data_title")
    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    @JsonProperty(value="road_name")
    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlotRequest)) {
            return false;
        }
        PlotRequest other = (PlotRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$dataTitle = this.getDataTitle();
        String other$dataTitle = other.getDataTitle();
        if (this$dataTitle == null ? other$dataTitle != null : !this$dataTitle.equals(other$dataTitle)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        return !(this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlotRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $dataTitle = this.getDataTitle();
        result = result * 59 + ($dataTitle == null ? 43 : $dataTitle.hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        return result;
    }

    public String toString() {
        return "PlotRequest(dataTitle=" + this.getDataTitle() + ", roadName=" + this.getRoadName() + ")";
    }
}

