/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.TimeRange
 *  com.etas.vaas.backend.utils.TimeUtils
 */
package com.etas.vaas.backend.utils;

import com.etas.vaas.backend.dto.TimeRange;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/*
 * Exception performing whole class analysis ignored.
 */
public final class TimeUtils {
    private static final String TIME_FORMAT_WITH_COLON = "yyyy-MM-dd HH:mm:ss.SSS";

    public static String convertTsToDatetime(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return dateTime.toString();
    }

    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(new Date(timestamp).toInstant(), ZoneId.of("Asia/Shanghai"));
    }

    public static long getHourTimestamp(int hour) {
        LocalDateTime current = LocalDateTime.now().minusHours(hour).truncatedTo(ChronoUnit.HOURS);
        return TimeUtils.toEpochMilli((LocalDateTime)current);
    }

    public static TimeRange getTimeRange(int hour) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime timeRangeLeft = currentTime.minusHours(23L).truncatedTo(ChronoUnit.HOURS);
        LocalDateTime timeRangeRight = currentTime;
        if (hour != 1 && hour != 0) {
            timeRangeRight = currentTime.minusHours(hour - 1).truncatedTo(ChronoUnit.HOURS);
        }
        long leftTimestamp = TimeUtils.toEpochMilli((LocalDateTime)timeRangeLeft);
        long rightTimestamp = TimeUtils.toEpochMilli((LocalDateTime)timeRangeRight);
        TimeRange timeRange = new TimeRange();
        timeRange.setLeft(leftTimestamp);
        timeRange.setRight(rightTimestamp);
        return timeRange;
    }

    public static long toEpochMilli(LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static String objToStr(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return time.format(formatter);
    }

    private TimeUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

