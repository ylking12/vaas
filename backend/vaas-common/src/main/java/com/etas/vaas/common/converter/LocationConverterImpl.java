/*
 * Decompiled with CFR 0.152.
 */
package com.etas.vaas.common.converter;

import com.etas.vaas.common.converter.LocationConverter;
import com.etas.vaas.common.dto.CachedVehiclePosition;
import com.etas.vaas.common.dto.LocationFrame;

public class LocationConverterImpl
implements LocationConverter {
    @Override
    public CachedVehiclePosition locationFrame2CacheVehiclePosition(LocationFrame frame) {
        if (frame == null) {
            return null;
        }
        CachedVehiclePosition cachedVehiclePosition = new CachedVehiclePosition();
        cachedVehiclePosition.setDateTime(this.convertTs(frame.getTimestamp()));
        cachedVehiclePosition.setLongitude(frame.getLongitude());
        cachedVehiclePosition.setLatitude(frame.getLatitude());
        cachedVehiclePosition.setTimestamp(frame.getTimestamp());
        return cachedVehiclePosition;
    }
}

