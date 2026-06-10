/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.enumeration.RoadEventType
 */
package com.etas.vaas.backend.enumeration;


/*
 * Exception performing whole class analysis ignored.
 */
public enum RoadEventType {
    BUMP("bumpy_event", "\u98a0\u7c38\u70b9"),
    SLIP("slippery_event", "\u6e7f\u6ed1\u70b9"),
    PONDING("ponding_event", "\u79ef\u6c34\u70b9"),
    LOW_ATTACHMENT("low_attachment_event", "\u4f4e\u9644\u7740\u70b9"),
    ICE("ice_event", "\u8def\u9762\u7ed3\u51b0\u70b9");

    private final String value;
    private final String valueZh;

    public String getValue() {
        return this.value;
    }

    public String getValueZh() {
        return this.valueZh;
    }

    public static String getValueZhByValue(String value) {
        for (RoadEventType type : RoadEventType.values()) {
            if (!type.getValue().equals(value)) continue;
            return type.getValueZh();
        }
        return "\u672a\u77e5";
    }

    private RoadEventType(String value, String valueZh) {
        this.value = value;
        this.valueZh = valueZh;
    }
}

