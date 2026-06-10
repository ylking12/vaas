/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.api.IServiceController
 *  com.etas.vaas.backend.component.DeviceServiceController
 *  com.etas.vaas.backend.component.DeviceServiceController$1
 *  com.etas.vaas.backend.configuration.SensorConfig$SdkInfo
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 *  rk.netDevice.sdk.p3.DeviceService
 *  rk.netDevice.sdk.p3.IDataListener
 */
package com.etas.vaas.backend.component;

import com.etas.vaas.backend.api.IServiceController;
import com.etas.vaas.backend.component.DeviceServiceController;
import com.etas.vaas.backend.configuration.SensorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rk.netDevice.sdk.p3.DeviceService;
import rk.netDevice.sdk.p2.IDataListener;

@Component
public class DeviceServiceController
implements IServiceController {
    private static final Logger log = LoggerFactory.getLogger(DeviceServiceController.class);
    IDataListener dataListener = null;
    private Thread thread = null;
    private DeviceService deviceService;

    public DeviceServiceController() {
        log.info("DeviceServiceController constructed");
    }

    public void start(SensorConfig.SdkInfo config) {
        log.info("start Device Service:{},host={},port={}", config.isEnabled(), config.getHost(), config.getPort());
        if (config.isEnabled()) {
            // TODO: SDK接入线程 - 原始代码使用了第三方 NetDeviceSDKP3，需厂商提供SDK后补充
            Runnable run = () -> log.info("DeviceService SDK thread started");
            if (this.thread != null && this.thread.isAlive()) {
                this.thread.interrupt();
            }
            this.thread = new Thread((Runnable)run);
            this.thread.start();
        }
    }

    public void stop() {
    }
}

