/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector.utils.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.etas.vaas.detector.utils;

import org.apache.commons.lang3.math.NumberUtils;

public final class StringUtils {
    public static Float strToFloat(String value) {
        return Float.valueOf(NumberUtils.toFloat((String)value, (float)Float.MIN_VALUE));
    }

    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

