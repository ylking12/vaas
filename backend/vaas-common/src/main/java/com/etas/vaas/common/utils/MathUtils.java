/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.CollectionUtils
 */
package com.etas.vaas.common.utils;

import com.etas.vaas.common.dto.CachedVehiclePosition;
import java.util.List;
import org.springframework.util.CollectionUtils;

public final class MathUtils {
    private static final double EARTH_RADIUS = 6371000.0;

    public static float pearsonCorrelation(List<Float> x, List<Float> y) {
        if (x.size() != y.size()) {
            throw new IllegalArgumentException(" pearsonCorrelation : Lists must have the same length");
        }
        float meanX = MathUtils.calculateMeanOfList(x);
        float meanY = MathUtils.calculateMeanOfList(y);
        float numerator = 0.0f;
        float denominatorX = 0.0f;
        float denominatorY = 0.0f;
        for (int i = 0; i < x.size(); ++i) {
            numerator += (x.get(i).floatValue() - meanX) * (y.get(i).floatValue() - meanY);
            denominatorX += (float)Math.pow(x.get(i).floatValue() - meanX, 2.0);
            denominatorY += (float)Math.pow(y.get(i).floatValue() - meanY, 2.0);
        }
        return numerator / (float)Math.sqrt(denominatorX * denominatorY);
    }

    public static float calculateMeanOfList(List<Float> data) {
        float sum = 0.0f;
        for (float value : data) {
            sum += value;
        }
        return sum / (float)data.size();
    }

    public static Float standardDeviationWithNullableValue(List<Float> inputList) {
        Float mean = MathUtils.meanOfListWithNullableValue(inputList);
        if (mean == null) {
            return null;
        }
        float variance = 0.0f;
        for (float value : inputList) {
            variance = (float)((double)variance + Math.pow(value - mean.floatValue(), 2.0));
        }
        return Float.valueOf((float)Math.sqrt(variance /= (float)inputList.size()));
    }

    public static Float meanOfListWithNullableValue(List<Float> inputList) {
        float sum = 0.0f;
        for (Float value : inputList) {
            if (value == null) {
                return null;
            }
            sum += value.floatValue();
        }
        return Float.valueOf(sum / (float)inputList.size());
    }

    public static double haversineDistance(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = Math.toRadians(lat1);
        double radLon1 = Math.toRadians(lon1);
        double radLat2 = Math.toRadians(lat2);
        double radLon2 = Math.toRadians(lon2);
        double deltaLat = radLat2 - radLat1;
        double deltaLon = radLon2 - radLon1;
        double a = Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0) + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(deltaLon / 2.0) * Math.sin(deltaLon / 2.0);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return 6371000.0 * c;
    }

    public static int binarySearchForCacheVehiclePosition(List<CachedVehiclePosition> vehiclePositionList, long eventTimestamp) {
        if (CollectionUtils.isEmpty(vehiclePositionList)) {
            throw new IllegalArgumentException("An empty CachedVehiclePosition coming into binary search!!!");
        }
        int left = 0;
        int right = vehiclePositionList.size() - 1;
        long minTimeDiff = Long.MAX_VALUE;
        int closestIndex = 0;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            CachedVehiclePosition current = vehiclePositionList.get(mid);
            long gpsTimestamp = current.getTimestamp();
            long timeDiff = Math.abs(gpsTimestamp - eventTimestamp);
            if (timeDiff < minTimeDiff) {
                minTimeDiff = timeDiff;
                closestIndex = mid;
            }
            if (gpsTimestamp < eventTimestamp) {
                left = mid + 1;
                continue;
            }
            right = mid - 1;
        }
        return closestIndex;
    }

    public static int binarySearchForTimestampIndex(List<Long> cachedLocationTimestamps, long eventTimestamp) {
        if (CollectionUtils.isEmpty(cachedLocationTimestamps)) {
            throw new IllegalArgumentException("An empty CachedVehiclePosition coming into binary search!!!");
        }
        int left = 0;
        int right = cachedLocationTimestamps.size() - 1;
        long minTimeDiff = Long.MAX_VALUE;
        int closestIndex = 0;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            long gpsTimestamp = cachedLocationTimestamps.get(mid);
            long timeDiff = Math.abs(gpsTimestamp - eventTimestamp);
            if (timeDiff < minTimeDiff) {
                minTimeDiff = timeDiff;
                closestIndex = mid;
            }
            if (gpsTimestamp < eventTimestamp) {
                left = mid + 1;
                continue;
            }
            right = mid - 1;
        }
        return closestIndex;
    }

    private MathUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

