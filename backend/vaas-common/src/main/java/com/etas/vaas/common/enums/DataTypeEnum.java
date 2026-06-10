/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.EnumValue
 */
package com.etas.vaas.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum DataTypeEnum {
    KT("kt710"),
    WIT("6a");

    @EnumValue
    private final String value;

    private DataTypeEnum(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static DataTypeEnum fromString(String value) {
        for (DataTypeEnum dataType : DataTypeEnum.values()) {
            if (!dataType.value.equalsIgnoreCase(value)) continue;
            return dataType;
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

