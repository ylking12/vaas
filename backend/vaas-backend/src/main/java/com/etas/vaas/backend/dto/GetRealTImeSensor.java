/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.GetRealTImeSensor
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetRealTImeSensor {
    @JsonProperty(value="road_name")
    private String roadName;

    public GetRealTImeSensor() {
    }

    public String getRoadName() {
        return this.roadName;
    }

    @JsonProperty(value="road_name")
    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GetRealTImeSensor)) {
            return false;
        }
        GetRealTImeSensor other = (GetRealTImeSensor)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.getRoadName() == other.getRoadName();
    }

    protected boolean canEqual(Object other) {
        return other instanceof GetRealTImeSensor;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getRoadName().hashCode();
        return result;
    }

    public String toString() {
        return "GetRealTImeSensor(roadName=" + this.getRoadName() + ")";
    }
}

