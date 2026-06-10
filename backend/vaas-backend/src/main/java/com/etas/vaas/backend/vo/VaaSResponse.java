/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.vo.VaaSResponse
 */
package com.etas.vaas.backend.vo;


/*
 * Exception performing whole class analysis ignored.
 */
public class VaaSResponse<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> VaaSResponse<T> success(T data) {
        int aec = 200;
        String message = "\u6210\u529f";
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            aec = 500;
            message = "\u7cfb\u7edf\u5f02\u5e38";
        }
        return VaaSResponse.restResult((Integer)aec, (String)message, data);
    }

    public static <T> VaaSResponse<T> error(String msg) {
        return VaaSResponse.restResult((Integer)500, (String)msg, null);
    }

    private static <T> VaaSResponse<T> restResult(Integer code, String msg, T data) {
        VaaSResponse apiResult = new VaaSResponse();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public String toString() {
        return "VaaSResponse(code=" + this.getCode() + ", msg=" + this.getMsg() + ", data=" + this.getData() + ")";
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

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }
}

