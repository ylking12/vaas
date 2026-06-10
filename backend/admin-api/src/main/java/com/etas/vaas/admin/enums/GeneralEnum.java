/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.ApiResponse
 *  com.etas.vaas.admin.enums.GeneralEnum
 *  com.fasterxml.jackson.annotation.JsonValue
 *  lombok.Generated
 */
package com.etas.vaas.admin.enums;

import com.etas.vaas.admin.ApiResponse;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GeneralEnum implements ApiResponse
{
    NOTHING_TO_UPDATE(Integer.valueOf(2001), "\u4e0e\u539f\u5185\u5bb9\u76f8\u540c"),
    UNKNOWN_ERROR(Integer.valueOf(-400), "\u672a\u77e5\u9519\u8bef"),
    SUCCESS(Integer.valueOf(201), "\u64cd\u4f5c\u6210\u529f"),
    FAILED(Integer.valueOf(-201), "\u64cd\u4f5c\u5931\u8d25");

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

    private GeneralEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

