/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.mapper.BaseMapper
 *  org.apache.ibatis.annotations.Select
 */
package com.etas.vaas.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.etas.vaas.common.entity.Weather;
import java.util.Optional;
import org.apache.ibatis.annotations.Select;

public interface WeatherMapper
extends BaseMapper<Weather> {
    @Select(value={"SELECT * FROM weather WHERE district_name = #{districtName} ORDER BY obs_time DESC LIMIT 1"})
    public Optional<Weather> getLastWeatherEntryByDistrictName(String var1);
}

