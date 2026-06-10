/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mapstruct.BeanMapping
 *  org.mapstruct.Mapper
 *  org.mapstruct.Mapping
 *  org.mapstruct.Mappings
 *  org.mapstruct.Named
 *  org.mapstruct.factory.Mappers
 */
package com.etas.vaas.common.converter;

import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dao.WeatherEventDao;
import com.etas.vaas.common.dto.KtVehicleEvent;
import com.etas.vaas.common.entity.Event;
import com.etas.vaas.common.utils.TimeUtils;
import java.time.LocalDateTime;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventConverter {
    public static final EventConverter INSTANCE = (EventConverter)Mappers.getMapper(EventConverter.class);

    @Named(value="convertTs")
    default public LocalDateTime convertTimestamp(Long ts) {
        if (ts == null) {
            return null;
        }
        return TimeUtils.timestampToLocalDateTime(ts);
    }

    @Mappings(value={@Mapping(target="sourceType", expression="java(com.etas.vaas.common.enums.SourceType.WEATHER_SENSOR)"), @Mapping(target="receivedTime", source="eventTimestamp", qualifiedByName={"convertTs"}), @Mapping(target="perceptionTime", source="eventTimestamp", qualifiedByName={"convertTs"}), @Mapping(target="inArea", constant="true"), @Mapping(target="id", ignore=true), @Mapping(target="eventTime", source="eventTimestamp", qualifiedByName={"convertTs"}), @Mapping(target="duplicated", constant="false")})
    public Event weatherEventDao2Event(WeatherEventDao var1);

    @Mappings(value={@Mapping(target="h3Hash", source="cellAddress"), @Mapping(target="sourceId", source="deviceId"), @Mapping(target="receivedTime", source="receivedTimestamp", qualifiedByName={"convertTs"}), @Mapping(target="perceptionTime", source="perceptionTimestamp", qualifiedByName={"convertTs"}), @Mapping(target="eventTime", source="eventTimestamp", qualifiedByName={"convertTs"}), @Mapping(target="id", ignore=true)})
    public Event vehicleEventDao2Event(VehicleEventDao var1);

    @BeanMapping(resultType=VehicleEventDao.class)
    public VehicleEventDao ktEvent2VehicleEventDao(KtVehicleEvent var1);
}

