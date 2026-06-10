/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.ApiResponse
 *  com.etas.vaas.admin.enums.ResponseEnum
 *  com.fasterxml.jackson.annotation.JsonValue
 *  lombok.Generated
 */
package com.etas.vaas.admin.enums;

import com.etas.vaas.admin.ApiResponse;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResponseEnum implements ApiResponse
{
    SAME_DEVICE_ID(Integer.valueOf(-101), "\u5b58\u5728\u76f8\u540c\u7684\u8bbe\u5907\u53f7"),
    SAME_KT_710_ID(Integer.valueOf(-102), "\u5b58\u5728\u76f8\u540c\u7684KT710 SN\u53f7"),
    SAME_PLATE(Integer.valueOf(-103), "\u5b58\u5728\u76f8\u540c\u7684\u8f66\u724c");

    private static final long serialVersionUID = 1L;
    @JsonValue
    private final Integer code;
    @JsonValue
    private final String msg;

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    private ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

