/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.controller.web.StatisticController
 *  com.etas.vaas.backend.dto.GetRealTImeSensor
 *  com.etas.vaas.backend.dto.request.PlotRequest
 *  com.etas.vaas.backend.service.web.StatisticService
 *  com.etas.vaas.backend.vo.PlotResp
 *  jakarta.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.CrossOrigin
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RestController
 */
package com.etas.vaas.backend.controller.web;

import com.etas.vaas.backend.dto.GetRealTImeSensor;
import com.etas.vaas.backend.dto.request.PlotRequest;
import com.etas.vaas.backend.service.web.StatisticService;
import com.etas.vaas.backend.vo.PlotResp;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class StatisticController {
    private static final Logger log = LoggerFactory.getLogger(StatisticController.class);
    @Resource
    private StatisticService statisticService;

    @PostMapping(value={"/get_covered_range"})
    public double[] getCoverageRange() {
        return this.statisticService.vehicleCoveredRange();
    }

    @PostMapping(value={"/get_real_time_sensor_data"})
    public ResponseEntity<Map<String, Object>> getSensorData(@RequestBody GetRealTImeSensor road) {
        return ResponseEntity.ok(this.statisticService.getRealTimeSensor(road.getRoadName()));
    }

    @PostMapping(value={"get_last24h_data_plot"})
    public ResponseEntity<List<PlotResp>> getPlotData(@RequestBody PlotRequest request) {
        return ResponseEntity.ok(this.statisticService.getPlotData(request));
    }
}

