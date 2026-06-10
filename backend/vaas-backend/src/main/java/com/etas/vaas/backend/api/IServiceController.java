/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.api.IServiceController
 *  com.etas.vaas.backend.configuration.SensorConfig$SdkInfo
 */
package com.etas.vaas.backend.api;

import com.etas.vaas.backend.configuration.SensorConfig;

public interface IServiceController {
    public void start(SensorConfig.SdkInfo var1);

    public void stop();
}

