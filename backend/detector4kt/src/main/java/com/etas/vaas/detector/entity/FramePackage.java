/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector.entity.FramePackage
 *  com.etas.vaas.detector.entity.FramePackage$RequestData
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.etas.vaas.detector.entity;

import com.etas.vaas.detector.entity.FramePackage;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FramePackage {
    @JsonProperty(value="data")
    private List<RequestData> data;
    @JsonProperty(value="packageSize")
    private int packageSize;
    @JsonProperty(value="received_at")
    private String receivedAt;

    public FramePackage() {
    }

    public List<RequestData> getData() {
        return this.data;
    }

    public int getPackageSize() {
        return this.packageSize;
    }

    public String getReceivedAt() {
        return this.receivedAt;
    }

    @JsonProperty(value="data")
    public void setData(List<RequestData> data) {
        this.data = data;
    }

    @JsonProperty(value="packageSize")
    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    @JsonProperty(value="received_at")
    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FramePackage)) {
            return false;
        }
        FramePackage other = (FramePackage)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getPackageSize() != other.getPackageSize()) {
            return false;
        }
        List this$data = this.getData();
        List other$data = other.getData();
        if (this$data == null ? other$data != null : !((Object)this$data).equals(other$data)) {
            return false;
        }
        String this$receivedAt = this.getReceivedAt();
        String other$receivedAt = other.getReceivedAt();
        return !(this$receivedAt == null ? other$receivedAt != null : !this$receivedAt.equals(other$receivedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof FramePackage;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPackageSize();
        List $data = this.getData();
        result = result * 59 + ($data == null ? 43 : ((Object)$data).hashCode());
        String $receivedAt = this.getReceivedAt();
        result = result * 59 + ($receivedAt == null ? 43 : $receivedAt.hashCode());
        return result;
    }

    public String toString() {
        return "FramePackage(data=" + this.getData() + ", packageSize=" + this.getPackageSize() + ", receivedAt=" + this.getReceivedAt() + ")";
    }

    public static class RequestData {
        private String sn;
        private String date;
        private List<StreamData> streamData;
        public String getSn() { return sn; }
        public void setSn(String sn) { this.sn = sn; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public List<StreamData> getStreamData() { return streamData; }
        public void setStreamData(List<StreamData> streamData) { this.streamData = streamData; }
        
        public static class StreamItem {
            private String name;
            private String value;
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public String getValue() { return value; }
            public void setValue(String value) { this.value = value; }
        }
    }
}

