/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.receiver.utils.GeoUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.etas.vaas.receiver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GeoUtils {
    private static final Logger log = LoggerFactory.getLogger(GeoUtils.class);
    private static final double EARTH_RADIUS = 6378.137;

    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);
        double s = 2.0 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0), 2.0) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0), 2.0)));
        s *= 6378.137;
        s = Math.round(s * 1000.0);
        return s /= 1000.0;
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
        return 6371000.0 * c / 1000.0;
    }

    private GeoUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

