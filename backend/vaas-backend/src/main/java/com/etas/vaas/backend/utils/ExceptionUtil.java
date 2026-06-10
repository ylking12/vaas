/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.utils.ExceptionUtil
 *  org.springframework.util.FastByteArrayOutputStream
 */
package com.etas.vaas.backend.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import org.springframework.util.FastByteArrayOutputStream;

/*
 * Exception performing whole class analysis ignored.
 */
public class ExceptionUtil {
    private ExceptionUtil() {
    }

    public static String stacktraceToString(Throwable throwable) {
        return ExceptionUtil.stacktraceToString(throwable, (int)3000);
    }

    public static String stacktraceToString(Throwable throwable, int limit) {
        FastByteArrayOutputStream baos = new FastByteArrayOutputStream();
        throwable.printStackTrace(new PrintStream((OutputStream)baos));
        String exceptionStr = baos.toString();
        int length = exceptionStr.length();
        if (limit < 0 || limit > length) {
            limit = length;
        }
        if (limit == length) {
            return exceptionStr;
        }
        return exceptionStr.substring(0, limit);
    }
}

