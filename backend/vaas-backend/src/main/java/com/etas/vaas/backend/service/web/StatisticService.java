/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.configuration.SensorConfig
 *  com.etas.vaas.backend.dto.request.PlotRequest
 *  com.etas.vaas.backend.service.web.StatisticService
 *  com.etas.vaas.backend.utils.RedisKeyUtils
 *  com.etas.vaas.backend.vo.PlotResp
 *  com.etas.vaas.common.utils.JsonUtils
 *  com.fasterxml.jackson.core.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.data.redis.core.StringRedisTemplate
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.web;

import com.etas.vaas.backend.configuration.SensorConfig;
import com.etas.vaas.backend.dto.request.PlotRequest;
import com.etas.vaas.backend.utils.RedisKeyUtils;
import com.etas.vaas.backend.vo.PlotResp;
import com.etas.vaas.common.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    private static final Logger log = LoggerFactory.getLogger(StatisticService.class);
    private static final String AA_ALL_CAR_CUMUL_MILEAGE = "social_car_cumul_mileage";
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SensorConfig sensorConfig;
    @Autowired
    private RedisKeyUtils redisKeyUtils;

    public double[] vehicleCoveredRange() {
        try {
            String totalAACumulativeMileage = (String)this.redisTemplate.opsForValue().get(AA_ALL_CAR_CUMUL_MILEAGE);
            if (totalAACumulativeMileage == null || totalAACumulativeMileage.isEmpty()) {
                totalAACumulativeMileage = "11.5";
            }
            if (totalAACumulativeMileage.endsWith("D")) {
                totalAACumulativeMileage = totalAACumulativeMileage.substring(0, totalAACumulativeMileage.length() - 1);
            }
            double totalAACumulativeMileageValue = Double.parseDouble(totalAACumulativeMileage);
            totalAACumulativeMileageValue = (double)Math.round(totalAACumulativeMileageValue * 10.0) / 10.0;
            return new double[]{1643.88, 3041.25, totalAACumulativeMileageValue};
        }
        catch (NumberFormatException e) {
            return new double[]{21.0, 3.0, 11.5};
        }
    }

    public Map<String, Object> getRealTimeSensor(String roadName) {
        int dscNumber;
        try { dscNumber = Integer.parseInt(roadName); } catch (NumberFormatException e) { dscNumber = 1; }
        HashMap<String, Object> response = new HashMap<String, Object>();
        Map<String, Object> sensorIds = (Map)this.sensorConfig.getRoadIds().get(String.valueOf(dscNumber));
        for (Map.Entry<String, Object> sensor : sensorIds.entrySet()) {
            String sensorName = String.valueOf(sensor.getKey());
            String sensorId = (String)sensor.getValue();
            String redisKey = this.redisKeyUtils.getMeasurementRedisKey(sensorId);
            Set<String> result = this.redisTemplate.opsForZSet().reverseRange(redisKey, 0L, 0L);
            result.forEach(measurementJSON -> {
                Map<String, Object> measurement = JsonUtils.parseJson(String.valueOf(measurementJSON), new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                this.formResponseFromMeasurement(measurement, response);
            });
        }
        if (!response.containsKey("airTemperature")) {
            response.put("airTemperature", 26);
        }
        if (!response.containsKey("roadSurfaceTemperature")) {
            response.put("roadSurfaceTemperature", 27);
        }
        if (!response.containsKey("relativeHumidity")) {
            response.put("relativeHumidity", 36);
        }
        if (!response.containsKey("levelOfGrip")) {
            response.put("levelOfGrip", 0.6);
        }
        if (!response.containsKey("waterLayerThickness")) {
            response.put("waterLayerThickness", 0.34);
        }
        if (!response.containsKey("rainIntensity")) {
            response.put("rainIntensity", 0);
        }
        return response;
    }

    private void formResponseFromMeasurement(Map<String, Object> measurement, Map<String, Object> response) {
        double rounded;
        double val;
        Object raw;
        if (measurement.containsKey("airTemperature")) {
            response.put("airTemperature", measurement.get("airTemperature"));
        }
        if (measurement.containsKey("relativeHumidity")) {
            response.put("relativeHumidity", measurement.get("relativeHumidity"));
        }
        if (measurement.containsKey("roadSurfaceTemperature")) {
            raw = measurement.get("roadSurfaceTemperature");
            if (raw instanceof Number) {
                val = ((Number)raw).doubleValue();
                rounded = (double)Math.round(val * 10.0) / 10.0;
                response.put("roadSurfaceTemperature", rounded);
            } else {
                response.put("roadSurfaceTemperature", raw);
            }
        }
        if (measurement.containsKey("pondingDepth")) {
            response.put("waterLayerThickness", measurement.get("pondingDepth"));
        }
        if (measurement.containsKey("TimeStamp")) {
            response.put("ts", measurement.get("TimeStamp"));
        }
        if (measurement.containsKey("windSpeed")) {
            response.put("meanWindSpeed", measurement.get("windSpeed"));
        }
        if (measurement.containsKey("windDirection")) {
            response.put("meanWindDirection", measurement.get("windDirection"));
        }
        if (measurement.containsKey("atmosphericVisibility")) {
            raw = measurement.get("atmosphericVisibility");
            if (raw instanceof Number) {
                val = ((Number)raw).doubleValue();
                rounded = (double)Math.round(val * 100.0) / 100.0;
                response.put("visibility", rounded);
            } else {
                response.put("visibility", raw);
            }
        }
        if (measurement.containsKey("roadConditions")) {
            response.put("roadStatus", measurement.get("roadConditions"));
        }
    }

    public List<PlotResp> getPlotData(PlotRequest request) {
        long now = Instant.now().getEpochSecond();
        String dataTitle = request.getDataTitle();
        String sensorName = (String)this.sensorConfig.getDataMatchSensor().get(dataTitle);
        Map sensors = (Map)this.sensorConfig.getRoadIds().get(request.getRoadName());
        String sensorId = (String)sensors.get(sensorName);
        String redisKey = this.redisKeyUtils.getMeasurementRedisKey(sensorId);
        ArrayList<PlotResp> response = new ArrayList<PlotResp>(25);
        for (int i = -24; i <= 0; ++i) {
            long end = (now + (long)(i * 3600)) * 1000L;
            long start = end - 3600000L;
            Set selectedData = this.redisTemplate.opsForZSet().reverseRangeByScore(redisKey, (double)start, (double)end, 0L, 1L);
            log.info("selectedData : {}", selectedData);
            if (selectedData.isEmpty()) continue;
            int index = i;
            selectedData.forEach(measurementJSON -> {
                Map<String, Object> measurement = JsonUtils.parseJson((String)measurementJSON, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                Object value = measurement.get(dataTitle);
                if (value instanceof Number) {
                    Object label = index == 0 ? "now" : index + "h";
                    response.add(new PlotResp((String)label, ((Number)value).floatValue()));
                }
            });
        }
        return response;
    }
}

