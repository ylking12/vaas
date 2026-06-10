/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.converter.FleetManagementConverter
 *  com.etas.vaas.admin.dto.AddCarMappingRequest
 *  com.etas.vaas.admin.dto.UpdateCarMappingRequest
 *  com.etas.vaas.common.entity.FleetManagement
 *  org.mapstruct.Mapper
 *  org.mapstruct.Mapping
 *  org.mapstruct.Mappings
 *  org.mapstruct.factory.Mappers
 */
package com.etas.vaas.admin.converter;

import com.etas.vaas.admin.dto.AddCarMappingRequest;
import com.etas.vaas.admin.dto.UpdateCarMappingRequest;
import com.etas.vaas.common.entity.FleetManagement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FleetManagementConverter {
    public static final FleetManagementConverter INSTANCE = (FleetManagementConverter)Mappers.getMapper(FleetManagementConverter.class);

    @Mappings(value={@Mapping(target="id", ignore=true), @Mapping(target="imei", source="deviceId")})
    public FleetManagement addRequest2Entity(AddCarMappingRequest var1);

    @Mapping(target="imei", source="deviceId")
    public FleetManagement updateRequest2Entity(UpdateCarMappingRequest var1);
}

