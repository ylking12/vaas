/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.excel.annotation.ExcelProperty
 *  com.etas.vaas.backend.dto.export.EventExcelTableDTO
 */
package com.etas.vaas.backend.dto.export;

import com.alibaba.excel.annotation.ExcelProperty;
import java.time.LocalDateTime;

public class EventExcelTableDTO {
    @ExcelProperty(value={"\u4e8b\u4ef6id"})
    private String eventId;
    @ExcelProperty(value={"\u4e0a\u62a5\u8005"})
    private String plateNumber;
    @ExcelProperty(value={"\u4e8b\u4ef6\u7c7b\u578b"})
    private String eventType;
    @ExcelProperty(value={"\u4e8b\u4ef6\u53d1\u751f\u65f6\u95f4"})
    private LocalDateTime eventTime;
    @ExcelProperty(value={"\u4e8b\u4ef6\u6240\u5728\u8def\u6bb5"})
    private String roadName;
    @ExcelProperty(value={"\u7ecf\u5ea6"})
    private Double longitude;
    @ExcelProperty(value={"\u7eac\u5ea6"})
    private Double latitude;

    public String getEventId() {
        return this.eventId;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public String getEventType() {
        return this.eventType;
    }

    public LocalDateTime getEventTime() {
        return this.eventTime;
    }

    public String getRoadName() {
        return this.roadName;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventExcelTableDTO)) {
            return false;
        }
        EventExcelTableDTO other = (EventExcelTableDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Double this$longitude = this.getLongitude();
        Double other$longitude = other.getLongitude();
        if (this$longitude == null ? other$longitude != null : !((Object)this$longitude).equals(other$longitude)) {
            return false;
        }
        Double this$latitude = this.getLatitude();
        Double other$latitude = other.getLatitude();
        if (this$latitude == null ? other$latitude != null : !((Object)this$latitude).equals(other$latitude)) {
            return false;
        }
        String this$eventId = this.getEventId();
        String other$eventId = other.getEventId();
        if (this$eventId == null ? other$eventId != null : !this$eventId.equals(other$eventId)) {
            return false;
        }
        String this$plateNumber = this.getPlateNumber();
        String other$plateNumber = other.getPlateNumber();
        if (this$plateNumber == null ? other$plateNumber != null : !this$plateNumber.equals(other$plateNumber)) {
            return false;
        }
        String this$eventType = this.getEventType();
        String other$eventType = other.getEventType();
        if (this$eventType == null ? other$eventType != null : !this$eventType.equals(other$eventType)) {
            return false;
        }
        LocalDateTime this$eventTime = this.getEventTime();
        LocalDateTime other$eventTime = other.getEventTime();
        if (this$eventTime == null ? other$eventTime != null : !((Object)this$eventTime).equals(other$eventTime)) {
            return false;
        }
        String this$roadName = this.getRoadName();
        String other$roadName = other.getRoadName();
        return !(this$roadName == null ? other$roadName != null : !this$roadName.equals(other$roadName));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventExcelTableDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Double $longitude = this.getLongitude();
        result = result * 59 + ($longitude == null ? 43 : ((Object)$longitude).hashCode());
        Double $latitude = this.getLatitude();
        result = result * 59 + ($latitude == null ? 43 : ((Object)$latitude).hashCode());
        String $eventId = this.getEventId();
        result = result * 59 + ($eventId == null ? 43 : $eventId.hashCode());
        String $plateNumber = this.getPlateNumber();
        result = result * 59 + ($plateNumber == null ? 43 : $plateNumber.hashCode());
        String $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        LocalDateTime $eventTime = this.getEventTime();
        result = result * 59 + ($eventTime == null ? 43 : ((Object)$eventTime).hashCode());
        String $roadName = this.getRoadName();
        result = result * 59 + ($roadName == null ? 43 : $roadName.hashCode());
        return result;
    }
}

