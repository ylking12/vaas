/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.dto.GetHeartbeatResp
 *  com.etas.vaas.admin.dto.GetHeartbeatResp$EachHeartbeatInfo
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;

import com.etas.vaas.admin.dto.GetHeartbeatResp;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@SuppressWarnings("unused")
public class GetHeartbeatResp {
    @JsonProperty(value="list")
    List<EachHeartbeatInfo> heartbeatInfoList;
    private int maxKtOnline = 0;
    private int maxMotionOnline = 0;
    private int maxLocationOnline = 0;
    private int currentKtOnline = 0;
    private int currentMotionOnline = 0;
    private int currentLocationOnline = 0;

    public GetHeartbeatResp() {
    }

    public List<EachHeartbeatInfo> getHeartbeatInfoList() {
        return this.heartbeatInfoList;
    }

    public int getMaxKtOnline() {
        return this.maxKtOnline;
    }

    public int getMaxMotionOnline() {
        return this.maxMotionOnline;
    }

    public int getMaxLocationOnline() {
        return this.maxLocationOnline;
    }

    public int getCurrentKtOnline() {
        return this.currentKtOnline;
    }

    public int getCurrentMotionOnline() {
        return this.currentMotionOnline;
    }

    public int getCurrentLocationOnline() {
        return this.currentLocationOnline;
    }

    @JsonProperty(value="list")
    public void setHeartbeatInfoList(List<EachHeartbeatInfo> heartbeatInfoList) {
        this.heartbeatInfoList = heartbeatInfoList;
    }

    public void setMaxKtOnline(int maxKtOnline) {
        this.maxKtOnline = maxKtOnline;
    }

    public void setMaxMotionOnline(int maxMotionOnline) {
        this.maxMotionOnline = maxMotionOnline;
    }

    public void setMaxLocationOnline(int maxLocationOnline) {
        this.maxLocationOnline = maxLocationOnline;
    }

    public void setCurrentKtOnline(int currentKtOnline) {
        this.currentKtOnline = currentKtOnline;
    }

    public void setCurrentMotionOnline(int currentMotionOnline) {
        this.currentMotionOnline = currentMotionOnline;
    }

    public void setCurrentLocationOnline(int currentLocationOnline) {
        this.currentLocationOnline = currentLocationOnline;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GetHeartbeatResp)) {
            return false;
        }
        GetHeartbeatResp other = (GetHeartbeatResp)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getMaxKtOnline() != other.getMaxKtOnline()) {
            return false;
        }
        if (this.getMaxMotionOnline() != other.getMaxMotionOnline()) {
            return false;
        }
        if (this.getMaxLocationOnline() != other.getMaxLocationOnline()) {
            return false;
        }
        if (this.getCurrentKtOnline() != other.getCurrentKtOnline()) {
            return false;
        }
        if (this.getCurrentMotionOnline() != other.getCurrentMotionOnline()) {
            return false;
        }
        if (this.getCurrentLocationOnline() != other.getCurrentLocationOnline()) {
            return false;
        }
        List this$heartbeatInfoList = this.getHeartbeatInfoList();
        List other$heartbeatInfoList = other.getHeartbeatInfoList();
        return !(this$heartbeatInfoList == null ? other$heartbeatInfoList != null : !((Object)this$heartbeatInfoList).equals(other$heartbeatInfoList));
    }

    protected boolean canEqual(Object other) {
        return other instanceof GetHeartbeatResp;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getMaxKtOnline();
        result = result * 59 + this.getMaxMotionOnline();
        result = result * 59 + this.getMaxLocationOnline();
        result = result * 59 + this.getCurrentKtOnline();
        result = result * 59 + this.getCurrentMotionOnline();
        result = result * 59 + this.getCurrentLocationOnline();
        List $heartbeatInfoList = this.getHeartbeatInfoList();
        result = result * 59 + ($heartbeatInfoList == null ? 43 : ((Object)$heartbeatInfoList).hashCode());
        return result;
    }

    public String toString() {
        return "GetHeartbeatResp(heartbeatInfoList=" + this.getHeartbeatInfoList() + ", maxKtOnline=" + this.getMaxKtOnline() + ", maxMotionOnline=" + this.getMaxMotionOnline() + ", maxLocationOnline=" + this.getMaxLocationOnline() + ", currentKtOnline=" + this.getCurrentKtOnline() + ", currentMotionOnline=" + this.getCurrentMotionOnline() + ", currentLocationOnline=" + this.getCurrentLocationOnline() + ")";
    }

    public static class EachHeartbeatInfo {
        private String deviceId;
        private String name;
        private int heartbeat;
        private boolean ktOnline;
        private boolean motionOnline;
        private boolean locationOnline;
        private Long ktLastOnlineTimestamp;
        private Long motionLastOnlineTimestamp;
        private Long locationLastOnlineTimestamp;
        private String ktLastOnlineTime;
        private String motionLastOnlineTime;
        private String locationLastOnlineTime;
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String v) { this.deviceId = v; }
        public String getName() { return name; }
        public void setName(String v) { this.name = v; }
        public int getHeartbeat() { return heartbeat; }
        public void setHeartbeat(int v) { this.heartbeat = v; }
        public boolean isKtOnline() { return ktOnline; }
        public void setKtOnline(boolean v) { this.ktOnline = v; }
        public boolean isMotionOnline() { return motionOnline; }
        public void setMotionOnline(boolean v) { this.motionOnline = v; }
        public boolean isLocationOnline() { return locationOnline; }
        public void setLocationOnline(boolean v) { this.locationOnline = v; }
        public Long getKtLastOnlineTimestamp() { return ktLastOnlineTimestamp; }
        public void setKtLastOnlineTimestamp(Long v) { this.ktLastOnlineTimestamp = v; }
        public Long getMotionLastOnlineTimestamp() { return motionLastOnlineTimestamp; }
        public void setMotionLastOnlineTimestamp(Long v) { this.motionLastOnlineTimestamp = v; }
        public Long getLocationLastOnlineTimestamp() { return locationLastOnlineTimestamp; }
        public void setLocationLastOnlineTimestamp(Long v) { this.locationLastOnlineTimestamp = v; }
        public String getKtLastOnlineTime() { return ktLastOnlineTime; }
        public void setKtLastOnlineTime(String v) { this.ktLastOnlineTime = v; }
        public String getMotionLastOnlineTime() { return motionLastOnlineTime; }
        public void setMotionLastOnlineTime(String v) { this.motionLastOnlineTime = v; }
        public String getLocationLastOnlineTime() { return locationLastOnlineTime; }
        public void setLocationLastOnlineTime(String v) { this.locationLastOnlineTime = v; }
    }
}

