/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.enumeration.EventSourceType
 *  com.fasterxml.jackson.annotation.JsonCreator
 */
package com.etas.vaas.backend.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

/*
 * Exception performing whole class analysis ignored.
 */
public enum EventSourceType {
    VEHICLE("vehicle"),
    SENSOR("vehicle"),
    OTHER("OTHER");

    private final String value;

    @JsonCreator
    public static EventSourceType fromValue(String value) {
        for (EventSourceType type : EventSourceType.values()) {
            if (!type.value.equals(value)) continue;
            return type;
        }
        return OTHER;
    }

    private EventSourceType(String value) {
        this.value = value;
    }
}

