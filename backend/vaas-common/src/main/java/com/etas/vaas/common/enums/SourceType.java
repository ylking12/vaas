/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.EnumValue
 */
package com.etas.vaas.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum SourceType {
    KT("kt710"),
    MOTION_SENSOR("motionSensor"),
    WEATHER_SENSOR("weatherSensor");

    @EnumValue
    private final String typeString;

    private SourceType(String str) {
        this.typeString = str;
    }

    public String getTypeString() {
        return this.typeString;
    }
}

