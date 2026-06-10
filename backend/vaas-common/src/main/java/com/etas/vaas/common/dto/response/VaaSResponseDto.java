/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.response;


public class VaaSResponseDto<T> {
    private int status;
    private String message;
    private T data;

    @SuppressWarnings("unchecked")
    public static <T> VaaSResponseDto<T> withNullData(int status, String message) {
        return (VaaSResponseDto<T>) new VaaSResponseDto<>(status, message, null);
    }

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof VaaSResponseDto)) {
            return false;
        }
        VaaSResponseDto other = (VaaSResponseDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getStatus() != other.getStatus()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        Object this$data = this.getData();
        Object other$data = other.getData();
        return this$data == null ? other$data == null : this$data.equals(other$data);
    }

    protected boolean canEqual(Object other) {
        return other instanceof VaaSResponseDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getStatus();
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        T $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        return "VaaSResponseDto(status=" + this.getStatus() + ", message=" + this.getMessage() + ", data=" + this.getData() + ")";
    }

    public VaaSResponseDto() {
    }

    @SuppressWarnings("unchecked")
    public VaaSResponseDto(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

