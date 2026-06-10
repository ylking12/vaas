/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.configuration.SensorConfig$DataType
 *  com.etas.vaas.backend.entity.SensorNodeData
 *  com.etas.vaas.backend.entity.SensorNodeData$1
 */
package com.etas.vaas.backend.entity;

import com.etas.vaas.backend.configuration.SensorConfig;
import com.etas.vaas.backend.entity.SensorNodeData;
import java.util.Date;

public class SensorNodeData {
    private Integer nodeId;
    private Date recordTime;
    private Float analog1;
    private Float analog2;
    private Float floatData;
    private Long unsignedInt32Data;

    public SensorNodeData(int nodeId, Date recordTime, float analog1, float analog2, float floatValue, long integerValue) {
        this.nodeId = nodeId;
        this.recordTime = recordTime;
        this.analog1 = Float.valueOf(analog1);
        this.analog2 = Float.valueOf(analog2);
        this.floatData = Float.valueOf(floatValue);
        this.unsignedInt32Data = integerValue;
    }

    public Object getData(SensorConfig.DataType dataType, float coefficient) {
        Float data = null;
        if (dataType != null) {
            switch (dataType) {
                case Analog1:
                    data = this.analog1 * coefficient;
                    break;
                case Analog2:
                    data = this.analog2 * coefficient;
                    break;
                case Float:
                    data = this.floatData * coefficient;
                    break;
                case UNSigned32INT:
                    data = (float)this.unsignedInt32Data * coefficient;
                    break;
            }
        }
        return data;
    }

    public Integer getNodeId() {
        return this.nodeId;
    }

    public Date getRecordTime() {
        return this.recordTime;
    }

    public Float getAnalog1() {
        return this.analog1;
    }

    public Float getAnalog2() {
        return this.analog2;
    }

    public Float getFloatData() {
        return this.floatData;
    }

    public Long getUnsignedInt32Data() {
        return this.unsignedInt32Data;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    public void setAnalog1(Float analog1) {
        this.analog1 = analog1;
    }

    public void setAnalog2(Float analog2) {
        this.analog2 = analog2;
    }

    public void setFloatData(Float floatData) {
        this.floatData = floatData;
    }

    public void setUnsignedInt32Data(Long unsignedInt32Data) {
        this.unsignedInt32Data = unsignedInt32Data;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SensorNodeData)) {
            return false;
        }
        SensorNodeData other = (SensorNodeData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$nodeId = this.getNodeId();
        Integer other$nodeId = other.getNodeId();
        if (this$nodeId == null ? other$nodeId != null : !((Object)this$nodeId).equals(other$nodeId)) {
            return false;
        }
        Float this$analog1 = this.getAnalog1();
        Float other$analog1 = other.getAnalog1();
        if (this$analog1 == null ? other$analog1 != null : !((Object)this$analog1).equals(other$analog1)) {
            return false;
        }
        Float this$analog2 = this.getAnalog2();
        Float other$analog2 = other.getAnalog2();
        if (this$analog2 == null ? other$analog2 != null : !((Object)this$analog2).equals(other$analog2)) {
            return false;
        }
        Float this$floatData = this.getFloatData();
        Float other$floatData = other.getFloatData();
        if (this$floatData == null ? other$floatData != null : !((Object)this$floatData).equals(other$floatData)) {
            return false;
        }
        Long this$unsignedInt32Data = this.getUnsignedInt32Data();
        Long other$unsignedInt32Data = other.getUnsignedInt32Data();
        if (this$unsignedInt32Data == null ? other$unsignedInt32Data != null : !((Object)this$unsignedInt32Data).equals(other$unsignedInt32Data)) {
            return false;
        }
        Date this$recordTime = this.getRecordTime();
        Date other$recordTime = other.getRecordTime();
        return !(this$recordTime == null ? other$recordTime != null : !((Object)this$recordTime).equals(other$recordTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SensorNodeData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $nodeId = this.getNodeId();
        result = result * 59 + ($nodeId == null ? 43 : ((Object)$nodeId).hashCode());
        Float $analog1 = this.getAnalog1();
        result = result * 59 + ($analog1 == null ? 43 : ((Object)$analog1).hashCode());
        Float $analog2 = this.getAnalog2();
        result = result * 59 + ($analog2 == null ? 43 : ((Object)$analog2).hashCode());
        Float $floatData = this.getFloatData();
        result = result * 59 + ($floatData == null ? 43 : ((Object)$floatData).hashCode());
        Long $unsignedInt32Data = this.getUnsignedInt32Data();
        result = result * 59 + ($unsignedInt32Data == null ? 43 : ((Object)$unsignedInt32Data).hashCode());
        Date $recordTime = this.getRecordTime();
        result = result * 59 + ($recordTime == null ? 43 : ((Object)$recordTime).hashCode());
        return result;
    }

    public String toString() {
        return "SensorNodeData(nodeId=" + this.getNodeId() + ", recordTime=" + this.getRecordTime() + ", analog1=" + this.getAnalog1() + ", analog2=" + this.getAnalog2() + ", floatData=" + this.getFloatData() + ", unsignedInt32Data=" + this.getUnsignedInt32Data() + ")";
    }
}

