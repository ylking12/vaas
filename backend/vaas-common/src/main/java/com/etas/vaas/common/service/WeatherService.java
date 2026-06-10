/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.Resource
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.common.service;

import com.etas.vaas.common.dto.response.VaaSResponseDto;
import com.etas.vaas.common.entity.Weather;
import com.etas.vaas.common.mapper.WeatherMapper;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    @Resource
    private WeatherMapper weatherMapper;
    private static final String WuXiShi = "\u65e0\u9521\u5e02";
    private static final String LiangXiQu = "\u6881\u6eaa\u533a";
    private static final String XiShanQu = "\u9521\u5c71\u533a";
    private static final String HuiShanQu = "\u60e0\u5c71\u533a";

    public VaaSResponseDto<Weather> getLastWeather() {
        Optional<Weather> lastWeatherEntry = this.weatherMapper.getLastWeatherEntryByDistrictName(WuXiShi);
        if (lastWeatherEntry.isPresent()) {
            return new VaaSResponseDto<Weather>(200, "successful retrieved data", lastWeatherEntry.get());
        }
        return new VaaSResponseDto<Weather>(-1, "not matched data", null);
    }

    public VaaSResponseDto<String> getRainIntensityByDistrictName(String districtName) {
        Optional<Weather> lastWeatherEntry = this.weatherMapper.getLastWeatherEntryByDistrictName(districtName);
        if (lastWeatherEntry.isPresent()) {
            String content = districtName + ":" + lastWeatherEntry.get().getPrecip();
            return new VaaSResponseDto<String>(200, "successful retrieved data", content);
        }
        return new VaaSResponseDto<String>(-1, "not matched data", null);
    }
}

