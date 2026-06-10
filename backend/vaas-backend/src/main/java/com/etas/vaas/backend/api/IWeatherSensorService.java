/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.api.IWeatherSensorService
 *  com.etas.vaas.backend.entity.SensorNodeDataEntity
 */
package com.etas.vaas.backend.api;

import com.etas.vaas.backend.entity.SensorNodeDataEntity;

public interface IWeatherSensorService {
    public void handlerSensorData(SensorNodeDataEntity var1);

    public void startSDK();
}

