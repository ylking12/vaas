/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.EnumValue
 *  com.fasterxml.jackson.annotation.JsonValue
 */
package com.etas.vaas.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventType {
    BUMP("bump", "\u98a0\u7c38\u70b9"),
    SLIP("slip", "\u6253\u6ed1\u70b9"),
    PONDING("ponding", "\u79ef\u6c34\u70b9"),
    LOW_FRICTION("lowFriction", "\u4f4e\u6469\u64e6\u70b9"),
    ICE("ice", "\u7ed3\u51b0\u70b9");

    @EnumValue
    @JsonValue
    private final String typeString;
    private final String chineseName;

    private EventType(String typeString, String chineseName) {
        this.typeString = typeString;
        this.chineseName = chineseName;
    }

    public String getChineseName() {
        return this.chineseName;
    }

    public String getTypeString() {
        return this.typeString;
    }
}

