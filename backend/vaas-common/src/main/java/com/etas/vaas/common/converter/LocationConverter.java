/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mapstruct.Mapper
 *  org.mapstruct.Mapping
 *  org.mapstruct.Mappings
 *  org.mapstruct.Named
 *  org.mapstruct.factory.Mappers
 */
package com.etas.vaas.common.converter;

import com.etas.vaas.common.dto.CachedVehiclePosition;
import com.etas.vaas.common.dto.LocationFrame;
import com.etas.vaas.common.utils.TimeUtils;
import java.time.LocalDateTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LocationConverter {
    public static final LocationConverter INSTANCE = (LocationConverter)Mappers.getMapper(LocationConverter.class);

    @Named(value="convertTs")
    default public LocalDateTime convertTs(Long ts) {
        if (ts == null) {
            return null;
        }
        return TimeUtils.timestampToLocalDateTime(ts);
    }

    @Mappings(value={@Mapping(target="speed", ignore=true), @Mapping(target="dateTime", source="timestamp", qualifiedByName={"convertTs"})})
    public CachedVehiclePosition locationFrame2CacheVehiclePosition(LocationFrame var1);
}

