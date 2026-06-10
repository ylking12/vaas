/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.IdType
 *  com.baomidou.mybatisplus.annotation.TableField
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 *  com.etas.vaas.receiver.entity.RedisKey
 */
package com.etas.vaas.receiver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value="redis_key")
public class RedisKey {
    @TableId(type=IdType.AUTO)
    private Integer id;
    @TableField(value="vehicle_speed_key")
    private String vehicleSpeedKey;
    @TableField(value="vehicle_location_key_prefix")
    private String vehicleLocationKeyPrefix;
    @TableField(value="vehicle_last_online_ts_key")
    private String vehicleLastOnlineTimestampKey;
    @TableField(value="bump_counter_key")
    private String bumpCounterKey;
    @TableField(value="slip_counter_key")
    private String slipCounterKey;
    @TableField(value="bump_event_key")
    private String bumpEventKey;
    @TableField(value="slip_event_key")
    private String slipEventKey;
    @TableField(value="ice_event_key")
    private String iceEventKey;
    @TableField(value="ponding_event_key")
    private String pondingEventKey;
    @TableField(value="low_attach_event_key")
    private String lowAttachmentEventKey;
    @TableField(value="road_seg_co_key")
    private String roadSegmentCoordinatesKey;
    @TableField(value="road_seg_map_key")
    private String roadSegmentMapKey;
    @TableField(value="event_topic")
    private String eventTopic;
    @TableField(value="motion_topic")
    private String motionTopic;
    @TableField(value="motion_queue")
    private String motionQueue;
    @TableField(value="kt_topic")
    private String ktTopic;
    @TableField(value="kt_queue")
    private String ktQueue;
    @TableField(value="vehicle_info_key_prefix")
    private String vehicleInfoPrefix;

    public RedisKey() {
    }

    public Integer getId() {
        return this.id;
    }

    public String getVehicleSpeedKey() {
        return this.vehicleSpeedKey;
    }

    public String getVehicleLocationKeyPrefix() {
        return this.vehicleLocationKeyPrefix;
    }

    public String getVehicleLastOnlineTimestampKey() {
        return this.vehicleLastOnlineTimestampKey;
    }

    public String getBumpCounterKey() {
        return this.bumpCounterKey;
    }

    public String getSlipCounterKey() {
        return this.slipCounterKey;
    }

    public String getBumpEventKey() {
        return this.bumpEventKey;
    }

    public String getSlipEventKey() {
        return this.slipEventKey;
    }

    public String getIceEventKey() {
        return this.iceEventKey;
    }

    public String getPondingEventKey() {
        return this.pondingEventKey;
    }

    public String getLowAttachmentEventKey() {
        return this.lowAttachmentEventKey;
    }

    public String getRoadSegmentCoordinatesKey() {
        return this.roadSegmentCoordinatesKey;
    }

    public String getRoadSegmentMapKey() {
        return this.roadSegmentMapKey;
    }

    public String getEventTopic() {
        return this.eventTopic;
    }

    public String getMotionTopic() {
        return this.motionTopic;
    }

    public String getMotionQueue() {
        return this.motionQueue;
    }

    public String getKtTopic() {
        return this.ktTopic;
    }

    public String getKtQueue() {
        return this.ktQueue;
    }

    public String getVehicleInfoPrefix() {
        return this.vehicleInfoPrefix;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setVehicleSpeedKey(String vehicleSpeedKey) {
        this.vehicleSpeedKey = vehicleSpeedKey;
    }

    public void setVehicleLocationKeyPrefix(String vehicleLocationKeyPrefix) {
        this.vehicleLocationKeyPrefix = vehicleLocationKeyPrefix;
    }

    public void setVehicleLastOnlineTimestampKey(String vehicleLastOnlineTimestampKey) {
        this.vehicleLastOnlineTimestampKey = vehicleLastOnlineTimestampKey;
    }

    public void setBumpCounterKey(String bumpCounterKey) {
        this.bumpCounterKey = bumpCounterKey;
    }

    public void setSlipCounterKey(String slipCounterKey) {
        this.slipCounterKey = slipCounterKey;
    }

    public void setBumpEventKey(String bumpEventKey) {
        this.bumpEventKey = bumpEventKey;
    }

    public void setSlipEventKey(String slipEventKey) {
        this.slipEventKey = slipEventKey;
    }

    public void setIceEventKey(String iceEventKey) {
        this.iceEventKey = iceEventKey;
    }

    public void setPondingEventKey(String pondingEventKey) {
        this.pondingEventKey = pondingEventKey;
    }

    public void setLowAttachmentEventKey(String lowAttachmentEventKey) {
        this.lowAttachmentEventKey = lowAttachmentEventKey;
    }

    public void setRoadSegmentCoordinatesKey(String roadSegmentCoordinatesKey) {
        this.roadSegmentCoordinatesKey = roadSegmentCoordinatesKey;
    }

    public void setRoadSegmentMapKey(String roadSegmentMapKey) {
        this.roadSegmentMapKey = roadSegmentMapKey;
    }

    public void setEventTopic(String eventTopic) {
        this.eventTopic = eventTopic;
    }

    public void setMotionTopic(String motionTopic) {
        this.motionTopic = motionTopic;
    }

    public void setMotionQueue(String motionQueue) {
        this.motionQueue = motionQueue;
    }

    public void setKtTopic(String ktTopic) {
        this.ktTopic = ktTopic;
    }

    public void setKtQueue(String ktQueue) {
        this.ktQueue = ktQueue;
    }

    public void setVehicleInfoPrefix(String vehicleInfoPrefix) {
        this.vehicleInfoPrefix = vehicleInfoPrefix;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RedisKey)) {
            return false;
        }
        RedisKey other = (RedisKey)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$id = this.getId();
        Integer other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        String this$vehicleSpeedKey = this.getVehicleSpeedKey();
        String other$vehicleSpeedKey = other.getVehicleSpeedKey();
        if (this$vehicleSpeedKey == null ? other$vehicleSpeedKey != null : !this$vehicleSpeedKey.equals(other$vehicleSpeedKey)) {
            return false;
        }
        String this$vehicleLocationKeyPrefix = this.getVehicleLocationKeyPrefix();
        String other$vehicleLocationKeyPrefix = other.getVehicleLocationKeyPrefix();
        if (this$vehicleLocationKeyPrefix == null ? other$vehicleLocationKeyPrefix != null : !this$vehicleLocationKeyPrefix.equals(other$vehicleLocationKeyPrefix)) {
            return false;
        }
        String this$vehicleLastOnlineTimestampKey = this.getVehicleLastOnlineTimestampKey();
        String other$vehicleLastOnlineTimestampKey = other.getVehicleLastOnlineTimestampKey();
        if (this$vehicleLastOnlineTimestampKey == null ? other$vehicleLastOnlineTimestampKey != null : !this$vehicleLastOnlineTimestampKey.equals(other$vehicleLastOnlineTimestampKey)) {
            return false;
        }
        String this$bumpCounterKey = this.getBumpCounterKey();
        String other$bumpCounterKey = other.getBumpCounterKey();
        if (this$bumpCounterKey == null ? other$bumpCounterKey != null : !this$bumpCounterKey.equals(other$bumpCounterKey)) {
            return false;
        }
        String this$slipCounterKey = this.getSlipCounterKey();
        String other$slipCounterKey = other.getSlipCounterKey();
        if (this$slipCounterKey == null ? other$slipCounterKey != null : !this$slipCounterKey.equals(other$slipCounterKey)) {
            return false;
        }
        String this$bumpEventKey = this.getBumpEventKey();
        String other$bumpEventKey = other.getBumpEventKey();
        if (this$bumpEventKey == null ? other$bumpEventKey != null : !this$bumpEventKey.equals(other$bumpEventKey)) {
            return false;
        }
        String this$slipEventKey = this.getSlipEventKey();
        String other$slipEventKey = other.getSlipEventKey();
        if (this$slipEventKey == null ? other$slipEventKey != null : !this$slipEventKey.equals(other$slipEventKey)) {
            return false;
        }
        String this$iceEventKey = this.getIceEventKey();
        String other$iceEventKey = other.getIceEventKey();
        if (this$iceEventKey == null ? other$iceEventKey != null : !this$iceEventKey.equals(other$iceEventKey)) {
            return false;
        }
        String this$pondingEventKey = this.getPondingEventKey();
        String other$pondingEventKey = other.getPondingEventKey();
        if (this$pondingEventKey == null ? other$pondingEventKey != null : !this$pondingEventKey.equals(other$pondingEventKey)) {
            return false;
        }
        String this$lowAttachmentEventKey = this.getLowAttachmentEventKey();
        String other$lowAttachmentEventKey = other.getLowAttachmentEventKey();
        if (this$lowAttachmentEventKey == null ? other$lowAttachmentEventKey != null : !this$lowAttachmentEventKey.equals(other$lowAttachmentEventKey)) {
            return false;
        }
        String this$roadSegmentCoordinatesKey = this.getRoadSegmentCoordinatesKey();
        String other$roadSegmentCoordinatesKey = other.getRoadSegmentCoordinatesKey();
        if (this$roadSegmentCoordinatesKey == null ? other$roadSegmentCoordinatesKey != null : !this$roadSegmentCoordinatesKey.equals(other$roadSegmentCoordinatesKey)) {
            return false;
        }
        String this$roadSegmentMapKey = this.getRoadSegmentMapKey();
        String other$roadSegmentMapKey = other.getRoadSegmentMapKey();
        if (this$roadSegmentMapKey == null ? other$roadSegmentMapKey != null : !this$roadSegmentMapKey.equals(other$roadSegmentMapKey)) {
            return false;
        }
        String this$eventTopic = this.getEventTopic();
        String other$eventTopic = other.getEventTopic();
        if (this$eventTopic == null ? other$eventTopic != null : !this$eventTopic.equals(other$eventTopic)) {
            return false;
        }
        String this$motionTopic = this.getMotionTopic();
        String other$motionTopic = other.getMotionTopic();
        if (this$motionTopic == null ? other$motionTopic != null : !this$motionTopic.equals(other$motionTopic)) {
            return false;
        }
        String this$motionQueue = this.getMotionQueue();
        String other$motionQueue = other.getMotionQueue();
        if (this$motionQueue == null ? other$motionQueue != null : !this$motionQueue.equals(other$motionQueue)) {
            return false;
        }
        String this$ktTopic = this.getKtTopic();
        String other$ktTopic = other.getKtTopic();
        if (this$ktTopic == null ? other$ktTopic != null : !this$ktTopic.equals(other$ktTopic)) {
            return false;
        }
        String this$ktQueue = this.getKtQueue();
        String other$ktQueue = other.getKtQueue();
        if (this$ktQueue == null ? other$ktQueue != null : !this$ktQueue.equals(other$ktQueue)) {
            return false;
        }
        String this$vehicleInfoPrefix = this.getVehicleInfoPrefix();
        String other$vehicleInfoPrefix = other.getVehicleInfoPrefix();
        return !(this$vehicleInfoPrefix == null ? other$vehicleInfoPrefix != null : !this$vehicleInfoPrefix.equals(other$vehicleInfoPrefix));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RedisKey;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        String $vehicleSpeedKey = this.getVehicleSpeedKey();
        result = result * 59 + ($vehicleSpeedKey == null ? 43 : $vehicleSpeedKey.hashCode());
        String $vehicleLocationKeyPrefix = this.getVehicleLocationKeyPrefix();
        result = result * 59 + ($vehicleLocationKeyPrefix == null ? 43 : $vehicleLocationKeyPrefix.hashCode());
        String $vehicleLastOnlineTimestampKey = this.getVehicleLastOnlineTimestampKey();
        result = result * 59 + ($vehicleLastOnlineTimestampKey == null ? 43 : $vehicleLastOnlineTimestampKey.hashCode());
        String $bumpCounterKey = this.getBumpCounterKey();
        result = result * 59 + ($bumpCounterKey == null ? 43 : $bumpCounterKey.hashCode());
        String $slipCounterKey = this.getSlipCounterKey();
        result = result * 59 + ($slipCounterKey == null ? 43 : $slipCounterKey.hashCode());
        String $bumpEventKey = this.getBumpEventKey();
        result = result * 59 + ($bumpEventKey == null ? 43 : $bumpEventKey.hashCode());
        String $slipEventKey = this.getSlipEventKey();
        result = result * 59 + ($slipEventKey == null ? 43 : $slipEventKey.hashCode());
        String $iceEventKey = this.getIceEventKey();
        result = result * 59 + ($iceEventKey == null ? 43 : $iceEventKey.hashCode());
        String $pondingEventKey = this.getPondingEventKey();
        result = result * 59 + ($pondingEventKey == null ? 43 : $pondingEventKey.hashCode());
        String $lowAttachmentEventKey = this.getLowAttachmentEventKey();
        result = result * 59 + ($lowAttachmentEventKey == null ? 43 : $lowAttachmentEventKey.hashCode());
        String $roadSegmentCoordinatesKey = this.getRoadSegmentCoordinatesKey();
        result = result * 59 + ($roadSegmentCoordinatesKey == null ? 43 : $roadSegmentCoordinatesKey.hashCode());
        String $roadSegmentMapKey = this.getRoadSegmentMapKey();
        result = result * 59 + ($roadSegmentMapKey == null ? 43 : $roadSegmentMapKey.hashCode());
        String $eventTopic = this.getEventTopic();
        result = result * 59 + ($eventTopic == null ? 43 : $eventTopic.hashCode());
        String $motionTopic = this.getMotionTopic();
        result = result * 59 + ($motionTopic == null ? 43 : $motionTopic.hashCode());
        String $motionQueue = this.getMotionQueue();
        result = result * 59 + ($motionQueue == null ? 43 : $motionQueue.hashCode());
        String $ktTopic = this.getKtTopic();
        result = result * 59 + ($ktTopic == null ? 43 : $ktTopic.hashCode());
        String $ktQueue = this.getKtQueue();
        result = result * 59 + ($ktQueue == null ? 43 : $ktQueue.hashCode());
        String $vehicleInfoPrefix = this.getVehicleInfoPrefix();
        result = result * 59 + ($vehicleInfoPrefix == null ? 43 : $vehicleInfoPrefix.hashCode());
        return result;
    }

    public String toString() {
        return "RedisKey(id=" + this.getId() + ", vehicleSpeedKey=" + this.getVehicleSpeedKey() + ", vehicleLocationKeyPrefix=" + this.getVehicleLocationKeyPrefix() + ", vehicleLastOnlineTimestampKey=" + this.getVehicleLastOnlineTimestampKey() + ", bumpCounterKey=" + this.getBumpCounterKey() + ", slipCounterKey=" + this.getSlipCounterKey() + ", bumpEventKey=" + this.getBumpEventKey() + ", slipEventKey=" + this.getSlipEventKey() + ", iceEventKey=" + this.getIceEventKey() + ", pondingEventKey=" + this.getPondingEventKey() + ", lowAttachmentEventKey=" + this.getLowAttachmentEventKey() + ", roadSegmentCoordinatesKey=" + this.getRoadSegmentCoordinatesKey() + ", roadSegmentMapKey=" + this.getRoadSegmentMapKey() + ", eventTopic=" + this.getEventTopic() + ", motionTopic=" + this.getMotionTopic() + ", motionQueue=" + this.getMotionQueue() + ", ktTopic=" + this.getKtTopic() + ", ktQueue=" + this.getKtQueue() + ", vehicleInfoPrefix=" + this.getVehicleInfoPrefix() + ")";
    }
}

