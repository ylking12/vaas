/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.api.IWeatherSensorService
 *  com.etas.vaas.backend.component.AppReadyListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.context.event.ApplicationReadyEvent
 *  org.springframework.context.ApplicationListener
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.backend.component;

import com.etas.vaas.backend.api.IWeatherSensorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppReadyListener
implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(AppReadyListener.class);
    @Autowired
    IWeatherSensorService weatherSensorService;

    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("\u2705 Application context initialization is DONE. Ready to serve requests!:{}", event.getTimeTaken());
        this.weatherSensorService.startSDK();
    }
}

