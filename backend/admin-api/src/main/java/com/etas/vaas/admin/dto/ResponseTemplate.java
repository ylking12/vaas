/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.ApiResponse
 *  com.etas.vaas.admin.dto.ResponseTemplate
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  lombok.Generated
 */
package com.etas.vaas.admin.dto;

import com.etas.vaas.admin.ApiResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseTemplate<T> {
    @JsonProperty(value="code")
    private Integer code;
    @JsonProperty(value="msg")
    private String msg;
    @JsonProperty(value="data")
    private T data;

    public ResponseTemplate(ApiResponse apiResponse, T data) {
        this.code = apiResponse.getCode();
        this.msg = apiResponse.getMsg();
        this.data = data;
    }

    public ResponseTemplate(ApiResponse apiResponse) {
        this.code = apiResponse.getCode();
        this.msg = apiResponse.getMsg();
    }

    public ResponseTemplate(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return (T)this.data;
    }

    @JsonProperty(value="code")
    public void setCode(Integer code) {
        this.code = code;
    }

    @JsonProperty(value="msg")
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonProperty(value="data")
    public void setData(T data) {
        this.data = data;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ResponseTemplate)) {
            return false;
        }
        ResponseTemplate other = (ResponseTemplate)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$code = this.getCode();
        Integer other$code = other.getCode();
        if (this$code == null ? other$code != null : !((Object)this$code).equals(other$code)) {
            return false;
        }
        String this$msg = this.getMsg();
        String other$msg = other.getMsg();
        if (this$msg == null ? other$msg != null : !this$msg.equals(other$msg)) {
            return false;
        }
        Object this$data = this.getData();
        Object other$data = other.getData();
        return !(this$data == null ? other$data != null : !this$data.equals(other$data));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ResponseTemplate;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : ((Object)$code).hashCode());
        String $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        return "ResponseTemplate(code=" + this.getCode() + ", msg=" + this.getMsg() + ", data=" + this.getData() + ")";
    }

    public ResponseTemplate(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseTemplate() {
    }
}

