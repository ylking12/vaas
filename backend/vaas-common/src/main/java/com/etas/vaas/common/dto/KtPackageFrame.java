/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  jakarta.validation.constraints.Min
 *  jakarta.validation.constraints.NotBlank
 *  jakarta.validation.constraints.NotEmpty
 *  jakarta.validation.constraints.NotNull
 */
package com.etas.vaas.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class KtPackageFrame {
    @JsonProperty(value="data")
    @NotEmpty(message="data array should not be empty")
    private @NotEmpty(message="data array should not be empty") List<RequestData> data;
    @JsonProperty(value="packageSize")
    @NotNull(message="packageSize should not be null")
    @Min(value=1L, message="packageSize should  > 0")
    private @NotNull(message="packageSize should not be null") @Min(value=1L, message="packageSize should  > 0") int packageSize;
    @JsonProperty(value="received_at")
    private String receivedAt;

    public KtPackageFrame() {
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
        if (!(o instanceof KtPackageFrame)) {
            return false;
        }
        KtPackageFrame other = (KtPackageFrame)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getPackageSize() != other.getPackageSize()) {
            return false;
        }
        List<RequestData> this$data = this.getData();
        List<RequestData> other$data = other.getData();
        if (this$data == null ? other$data != null : !((Object)this$data).equals(other$data)) {
            return false;
        }
        String this$receivedAt = this.getReceivedAt();
        String other$receivedAt = other.getReceivedAt();
        return !(this$receivedAt == null ? other$receivedAt != null : !this$receivedAt.equals(other$receivedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof KtPackageFrame;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPackageSize();
        List<RequestData> $data = this.getData();
        result = result * 59 + ($data == null ? 43 : ((Object)$data).hashCode());
        String $receivedAt = this.getReceivedAt();
        result = result * 59 + ($receivedAt == null ? 43 : $receivedAt.hashCode());
        return result;
    }

    public String toString() {
        return "KtPackageFrame(data=" + this.getData() + ", packageSize=" + this.getPackageSize() + ", receivedAt=" + this.getReceivedAt() + ")";
    }

    public static class RequestData {
        @JsonProperty(value="date")
        private String date;
        @JsonProperty(value="sn")
        @NotBlank(message="sn can not be blank")
        private @NotBlank(message="sn can not be blank") String sn;
        @JsonProperty(value="stream_data")
        private List<StreamItem> streamData;

        public RequestData() {
        }

        public String getDate() {
            return this.date;
        }

        public String getSn() {
            return this.sn;
        }

        public List<StreamItem> getStreamData() {
            return this.streamData;
        }

        @JsonProperty(value="date")
        public void setDate(String date) {
            this.date = date;
        }

        @JsonProperty(value="sn")
        public void setSn(String sn) {
            this.sn = sn;
        }

        @JsonProperty(value="stream_data")
        public void setStreamData(List<StreamItem> streamData) {
            this.streamData = streamData;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof RequestData)) {
                return false;
            }
            RequestData other = (RequestData)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$date = this.getDate();
            String other$date = other.getDate();
            if (this$date == null ? other$date != null : !this$date.equals(other$date)) {
                return false;
            }
            String this$sn = this.getSn();
            String other$sn = other.getSn();
            if (this$sn == null ? other$sn != null : !this$sn.equals(other$sn)) {
                return false;
            }
            List<StreamItem> this$streamData = this.getStreamData();
            List<StreamItem> other$streamData = other.getStreamData();
            return !(this$streamData == null ? other$streamData != null : !((Object)this$streamData).equals(other$streamData));
        }

        protected boolean canEqual(Object other) {
            return other instanceof RequestData;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $date = this.getDate();
            result = result * 59 + ($date == null ? 43 : $date.hashCode());
            String $sn = this.getSn();
            result = result * 59 + ($sn == null ? 43 : $sn.hashCode());
            List<StreamItem> $streamData = this.getStreamData();
            result = result * 59 + ($streamData == null ? 43 : ((Object)$streamData).hashCode());
            return result;
        }

        public String toString() {
            return "KtPackageFrame.RequestData(date=" + this.getDate() + ", sn=" + this.getSn() + ", streamData=" + this.getStreamData() + ")";
        }

        public static class StreamItem {
            @JsonProperty(value="name")
            private String name;
            @JsonProperty(value="value")
            private String value;

            public StreamItem() {
            }

            public String getName() {
                return this.name;
            }

            public String getValue() {
                return this.value;
            }

            @JsonProperty(value="name")
            public void setName(String name) {
                this.name = name;
            }

            @JsonProperty(value="value")
            public void setValue(String value) {
                this.value = value;
            }

            public boolean equals(Object o) {
                if (o == this) {
                    return true;
                }
                if (!(o instanceof StreamItem)) {
                    return false;
                }
                StreamItem other = (StreamItem)o;
                if (!other.canEqual(this)) {
                    return false;
                }
                String this$name = this.getName();
                String other$name = other.getName();
                if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
                    return false;
                }
                String this$value = this.getValue();
                String other$value = other.getValue();
                return !(this$value == null ? other$value != null : !this$value.equals(other$value));
            }

            protected boolean canEqual(Object other) {
                return other instanceof StreamItem;
            }

            public int hashCode() {
                int PRIME = 59;
                int result = 1;
                String $name = this.getName();
                result = result * 59 + ($name == null ? 43 : $name.hashCode());
                String $value = this.getValue();
                result = result * 59 + ($value == null ? 43 : $value.hashCode());
                return result;
            }

            public String toString() {
                return "KtPackageFrame.RequestData.StreamItem(name=" + this.getName() + ", value=" + this.getValue() + ")";
            }
        }
    }
}

