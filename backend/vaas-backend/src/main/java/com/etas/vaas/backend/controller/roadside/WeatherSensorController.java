/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.controller.roadside.WeatherSensorController
 *  com.etas.vaas.backend.entity.SensorNodeDataEntity
 *  com.etas.vaas.backend.service.roadside.WeatherSensorService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.backend.controller.roadside;

import com.etas.vaas.backend.entity.SensorNodeDataEntity;
import com.etas.vaas.backend.service.roadside.WeatherSensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/post_realtime_sensor_data"})
public class WeatherSensorController {
    @Autowired
    private final WeatherSensorService weatherSensorService;

    @PostMapping
    public ResponseEntity<String> receiveWeatherData(@RequestBody SensorNodeDataEntity data) {
        this.weatherSensorService.handlerSensorData(data);
        return ResponseEntity.ok("Weather data received and processed.");
    }

    public WeatherSensorController(WeatherSensorService weatherSensorService) {
        this.weatherSensorService = weatherSensorService;
    }
}

