/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.admin.converter.FleetManagementConverter
 *  com.etas.vaas.admin.converter.FleetManagementConverterImpl
 *  com.etas.vaas.admin.dto.AddCarMappingRequest
 *  com.etas.vaas.admin.dto.UpdateCarMappingRequest
 *  com.etas.vaas.common.entity.FleetManagement
 */
package com.etas.vaas.admin.converter;

import com.etas.vaas.admin.converter.FleetManagementConverter;
import com.etas.vaas.admin.dto.AddCarMappingRequest;
import com.etas.vaas.admin.dto.UpdateCarMappingRequest;
import com.etas.vaas.common.entity.FleetManagement;

public class FleetManagementConverterImpl
implements FleetManagementConverter {
    public FleetManagement addRequest2Entity(AddCarMappingRequest dto) {
        if (dto == null) {
            return null;
        }
        FleetManagement fleetManagement = new FleetManagement();
        fleetManagement.setImei(dto.getDeviceId());
        fleetManagement.setKt710Id(dto.getKt710Id());
        fleetManagement.setPlate(dto.getPlate());
        fleetManagement.setGroupId(dto.getGroupId());
        fleetManagement.setBumpEnable(dto.isBumpEnable());
        fleetManagement.setSlipEnable(dto.isSlipEnable());
        fleetManagement.setSimId(dto.getSimId());
        fleetManagement.setBrandModel(dto.getBrandModel());
        fleetManagement.setReject(dto.isReject());
        return fleetManagement;
    }

    public FleetManagement updateRequest2Entity(UpdateCarMappingRequest dto) {
        if (dto == null) {
            return null;
        }
        FleetManagement fleetManagement = new FleetManagement();
        fleetManagement.setImei(dto.getDeviceId());
        fleetManagement.setId(dto.getId());
        fleetManagement.setKt710Id(dto.getKt710Id());
        fleetManagement.setPlate(dto.getPlate());
        fleetManagement.setGroupId(dto.getGroupId());
        fleetManagement.setBumpEnable(dto.isBumpEnable());
        fleetManagement.setSlipEnable(dto.isSlipEnable());
        fleetManagement.setSimId(dto.getSimId());
        fleetManagement.setBrandModel(dto.getBrandModel());
        fleetManagement.setReject(dto.isReject());
        return fleetManagement;
    }
}

