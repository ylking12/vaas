/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.component.District
 *  com.etas.vaas.backend.controller.web.WeatherController
 *  com.etas.vaas.backend.dto.Centroid
 *  com.etas.vaas.common.dto.response.VaaSResponseDto
 *  com.etas.vaas.common.entity.Weather
 *  com.etas.vaas.common.service.WeatherService
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.GetMapping
 *  org.springframework.web.bind.annotation.PathVariable
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.backend.controller.web;

import com.etas.vaas.backend.component.District;
import com.etas.vaas.backend.dto.Centroid;
import com.etas.vaas.common.dto.response.VaaSResponseDto;
import com.etas.vaas.common.entity.Weather;
import com.etas.vaas.common.service.WeatherService;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class WeatherController {
    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);
    @Resource
    private WeatherService weatherService;
    @Resource
    private District district;

    @GetMapping(value={"/get_weather"})
    public VaaSResponseDto<Weather> getWeather() {
        return this.weatherService.getLastWeather();
    }

    @GetMapping(value={"/get-rain-points"})
    public VaaSResponseDto<List<Centroid>> getRainPoint() {
        List centroids = this.district.getCentroids();
        return new VaaSResponseDto(200, "ok", centroids);
    }

    @GetMapping(value={"/get-rain-intensity/{districtName}"})
    public VaaSResponseDto<String> getRainIntensity(@PathVariable String districtName) {
        return this.weatherService.getRainIntensityByDistrictName(districtName);
    }
}

