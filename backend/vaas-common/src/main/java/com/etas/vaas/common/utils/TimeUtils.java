/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.etas.vaas.common.utils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TimeUtils {
    private static final Logger log = LoggerFactory.getLogger(TimeUtils.class);
    private static final String TIME_FORMAT_WITH_DASH = "yyyy_MM_dd_HH_mm_ss_SSS";
    private static final String TIME_FORMAT_WITH_COLON = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String ERROR_MESSAGE = "Invalid date format: ";

    public static long strToTimestamp(String t, String tz) {
        String timeFormat = t.contains(":") ? TIME_FORMAT_WITH_COLON : TIME_FORMAT_WITH_DASH;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        try {
            LocalDateTime dateTime = LocalDateTime.parse(t, formatter);
            return dateTime.atZone(ZoneId.of(tz)).toInstant().toEpochMilli();
        }
        catch (DateTimeParseException exception) {
            log.error("{} {}", ERROR_MESSAGE, (Object)t);
            throw exception;
        }
    }

    public static LocalDateTime strToObj(String timeString) {
        if (timeString.contains(":")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_WITH_COLON);
            try {
                return LocalDateTime.parse(timeString, formatter);
            }
            catch (DateTimeParseException e) {
                throw new IllegalArgumentException(ERROR_MESSAGE + timeString, e);
            }
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_WITH_DASH);
        try {
            return LocalDateTime.parse(timeString, formatter);
        }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException(ERROR_MESSAGE + timeString, e);
        }
    }

    public static String objToStr(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_WITH_COLON);
        return time.format(formatter);
    }

    public static Long objToTimestamp(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(new Date(timestamp).toInstant(), ZoneId.of("Asia/Shanghai"));
    }

    public static String formatStr(String timeStr) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT_WITH_DASH);
        LocalDateTime dateTime = LocalDateTime.parse(timeStr, inputFormatter);
        return TimeUtils.objToStr(dateTime);
    }

    public static String timestampToStr(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_WITH_COLON);
        return sdf.format(date);
    }

    public static String convertTsToDatetime(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(timestamp), ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_WITH_COLON);
        return dateTime.format(formatter);
    }

    public static float countTimeDiff(String currentData, String previousData) {
        LocalDateTime currentDate = TimeUtils.strToObj(currentData);
        LocalDateTime previousDate = TimeUtils.strToObj(previousData);
        long deltaMilliseconds = Duration.between(currentDate, previousDate).toMillis();
        return (float)Math.abs((double)deltaMilliseconds / 1000.0);
    }

    private TimeUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

