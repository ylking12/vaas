/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.configuration.SensorConfig$SensorType
 *  com.etas.vaas.backend.entity.SensorNodeData
 *  com.etas.vaas.backend.entity.SensorNodeDataEntity
 */
package com.etas.vaas.backend.entity;

import com.etas.vaas.backend.configuration.SensorConfig;
import com.etas.vaas.backend.entity.SensorNodeData;
import java.util.ArrayList;
import java.util.List;

public class SensorNodeDataEntity {
    private int sensorId;
    private SensorConfig.SensorType sensorType;
    private Short coordinateType;
    private Float latitude;
    private Float longitude;
    private List<SensorNodeData> nodeList = new ArrayList();

    public SensorNodeDataEntity() {
    }

    public SensorNodeDataEntity(int sensorId, SensorConfig.SensorType sensorType, short coordinateType, float latitude, float longitude) {
        this.sensorId = sensorId;
        this.sensorType = sensorType;
        this.coordinateType = coordinateType;
        this.latitude = Float.valueOf(latitude);
        this.longitude = Float.valueOf(longitude);
    }

    public int getSensorId() {
        return this.sensorId;
    }

    public SensorConfig.SensorType getSensorType() {
        return this.sensorType;
    }

    public Short getCoordinateType() {
        return this.coordinateType;
    }

    public Float getLatitude() {
        return this.latitude;
    }

    public Float getLongitude() {
        return this.longitude;
    }

    public List<SensorNodeData> getNodeList() {
        return this.nodeList;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public void setSensorType(SensorConfig.SensorType sensorType) {
        this.sensorType = sensorType;
    }

    public void setCoordinateType(Short coordinateType) {
        this.coordinateType = coordinateType;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public void setNodeList(List<SensorNodeData> nodeList) {
        this.nodeList = nodeList;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SensorNodeDataEntity)) {
            return false;
        }
        SensorNodeDataEntity other = (SensorNodeDataEntity)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getSensorId() != other.getSensorId()) {
            return false;
        }
        Short this$coordinateType = this.getCoordinateType();
        Short other$coordinateType = other.getCoordinateType();
        if (this$coordinateType == null ? other$coordinateType != null : !((Object)this$coordinateType).equals(other$coordinateType)) {
            return false;
        }
        Float this$latitude = this.getLatitude();
        Float other$latitude = other.getLatitude();
        if (this$latitude == null ? other$latitude != null : !((Object)this$latitude).equals(other$latitude)) {
            return false;
        }
        Float this$longitude = this.getLongitude();
        Float other$longitude = other.getLongitude();
        if (this$longitude == null ? other$longitude != null : !((Object)this$longitude).equals(other$longitude)) {
            return false;
        }
        SensorConfig.SensorType this$sensorType = this.getSensorType();
        SensorConfig.SensorType other$sensorType = other.getSensorType();
        if (this$sensorType == null ? other$sensorType != null : !this$sensorType.equals(other$sensorType)) {
            return false;
        }
        List this$nodeList = this.getNodeList();
        List other$nodeList = other.getNodeList();
        return !(this$nodeList == null ? other$nodeList != null : !((Object)this$nodeList).equals(other$nodeList));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SensorNodeDataEntity;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getSensorId();
        Short $coordinateType = this.getCoordinateType();
        result = result * 59 + ($coordinateType == null ? 43 : ((Object)$coordinateType).hashCode());
        Float $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        Float $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        SensorConfig.SensorType $sensorType = this.getSensorType();
        result = result * 59 + ($sensorType == null ? 43 : $sensorType.hashCode());
        List $nodeList = this.getNodeList();
        result = result * 59 + ($nodeList == null ? 43 : ((Object)$nodeList).hashCode());
        return result;
    }

    public String toString() {
        return "SensorNodeDataEntity(sensorId=" + this.getSensorId() + ", sensorType=" + this.getSensorType() + ", coordinateType=" + this.getCoordinateType() + ", latitude=" + this.getLatitude() + ", longitude=" + this.getLongitude() + ", nodeList=" + this.getNodeList() + ")";
    }
}

