/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
 *  com.baomidou.mybatisplus.core.metadata.IPage
 *  com.baomidou.mybatisplus.core.toolkit.support.SFunction
 *  com.baomidou.mybatisplus.extension.plugins.pagination.Page
 *  com.etas.vaas.admin.component.HeartbeatComponent
 *  com.etas.vaas.admin.converter.FleetManagementConverter
 *  com.etas.vaas.admin.dto.AddCarMappingRequest
 *  com.etas.vaas.admin.dto.GetCarMappingListRequest
 *  com.etas.vaas.admin.dto.GetCarMappingListResp
 *  com.etas.vaas.admin.dto.GetCarMappingListResp$EachFleetManagement
 *  com.etas.vaas.admin.dto.GetHeartbeatResp
 *  com.etas.vaas.admin.dto.Pagination
 *  com.etas.vaas.admin.dto.UpdateCarMappingRequest
 *  com.etas.vaas.admin.exception.AdminException$SameDeviceIdError
 *  com.etas.vaas.admin.exception.AdminException$SameKT710SNError
 *  com.etas.vaas.admin.exception.AdminException$SamePlateError
 *  com.etas.vaas.admin.exception.GeneralException$DoNothing
 *  com.etas.vaas.admin.exception.GeneralException$UnknownError
 *  com.etas.vaas.admin.service.AdminService
 *  com.etas.vaas.common.entity.BrandModel
 *  com.etas.vaas.common.entity.FleetManagement
 *  com.etas.vaas.common.mapper.BrandModelMapper
 *  com.etas.vaas.common.mapper.FleetManagementMapper
 *  com.etas.vaas.common.utils.RedisUtils
 *  jakarta.annotation.Resource
 *  lombok.Generated
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Service
 *  org.springframework.transaction.annotation.Transactional
 */
package com.etas.vaas.admin.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.etas.vaas.admin.component.HeartbeatComponent;
import com.etas.vaas.admin.converter.FleetManagementConverter;
import com.etas.vaas.admin.dto.AddCarMappingRequest;
import com.etas.vaas.admin.dto.GetCarMappingListRequest;
import com.etas.vaas.admin.dto.GetCarMappingListResp;
import com.etas.vaas.admin.dto.GetHeartbeatResp;
import com.etas.vaas.admin.dto.Pagination;
import com.etas.vaas.admin.dto.UpdateCarMappingRequest;
import com.etas.vaas.admin.exception.AdminException;
import com.etas.vaas.admin.exception.GeneralException;
import com.etas.vaas.common.entity.BrandModel;
import com.etas.vaas.common.entity.FleetManagement;
import com.etas.vaas.common.mapper.BrandModelMapper;
import com.etas.vaas.common.mapper.FleetManagementMapper;
import com.etas.vaas.common.utils.RedisUtils;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class AdminService {
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    @Resource
    private FleetManagementMapper fleetManagementMapper;
    @Resource
    private BrandModelMapper brandModelMapper;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HeartbeatComponent heartbeatComponent;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public GetCarMappingListResp doGetCarMappingList(GetCarMappingListRequest request) {
        ArrayList<GetCarMappingListResp.EachFleetManagement> eachFleetManagementList = new ArrayList<GetCarMappingListResp.EachFleetManagement>();
        LambdaQueryWrapper<FleetManagement> wrapper = new LambdaQueryWrapper<FleetManagement>();
        wrapper.like(StringUtils.isNotBlank((CharSequence)request.getDeviceId()), FleetManagement::getImei, (Object)request.getDeviceId());
        wrapper.like(StringUtils.isNotBlank((CharSequence)request.getKt710Id()), FleetManagement::getKt710Id, (Object)request.getKt710Id());
        wrapper.like(StringUtils.isNotBlank((CharSequence)request.getPlate()), FleetManagement::getPlate, (Object)request.getPlate());
        wrapper.eq(ObjectUtils.isNotEmpty((Object)request.getGroupId()), FleetManagement::getGroupId, (Object)request.getGroupId());
        wrapper.eq(ObjectUtils.isNotEmpty((Object)request.getReject()), FleetManagement::isReject, (Object)request.getReject());
        wrapper.eq(ObjectUtils.isNotEmpty((Object)request.getBumpEnable()), FleetManagement::isBumpEnable, (Object)request.getBumpEnable());
        wrapper.eq(ObjectUtils.isNotEmpty((Object)request.getSlipEnable()), FleetManagement::isSlipEnable, (Object)request.getSlipEnable());
        wrapper.like(StringUtils.isNotBlank((CharSequence)request.getSimId()), FleetManagement::getSimId, (Object)request.getSimId());
        wrapper.eq(StringUtils.isNotBlank((CharSequence)request.getBrandModel()), FleetManagement::getBrandModel, (Object)request.getBrandModel());
        wrapper.orderByDesc(FleetManagement::getId);
        Pagination pagination = request.getPagination();
        Page<FleetManagement> page = new Page<FleetManagement>(pagination.getCurrentPage().longValue(), pagination.getPageSize().longValue());
        page = this.fleetManagementMapper.selectPage(page, wrapper);
        pagination.setTotal(Long.valueOf(page.getTotal()));
        for (int i = 0; i < page.getRecords().size(); ++i) {
            FleetManagement each = page.getRecords().get(i);
            GetCarMappingListResp.EachFleetManagement eachInResp = new GetCarMappingListResp.EachFleetManagement();
            eachInResp.setNo(Long.valueOf(pagination.getPageSize() * (pagination.getCurrentPage() - 1L) + (long)i + 1L));
            eachInResp.setId(each.getId());
            eachInResp.setImei(each.getImei());
            eachInResp.setBrandModel(each.getBrandModel());
            eachInResp.setBumpEnable(each.isBumpEnable());
            eachInResp.setSlipEnable(each.isSlipEnable());
            eachInResp.setSimId(each.getSimId());
            eachInResp.setPlate(each.getPlate());
            eachInResp.setKt710Id(each.getKt710Id());
            eachInResp.setGroupId(each.getGroupId());
            eachInResp.setReject(each.isReject());
            eachInResp.setUpdateAt(each.getUpdateAt());
            eachFleetManagementList.add(eachInResp);
        }
        GetCarMappingListResp resp = new GetCarMappingListResp();
        resp.setPagination(pagination);
        resp.setFleetManagementList(eachFleetManagementList);
        return resp;
    }

    @Transactional
    public boolean doUpdateCarMapping(UpdateCarMappingRequest request) {
        FleetManagement entity;
        FleetManagement currentEntity = this.fleetManagementMapper.selectById(request.getId());
        String currentDeviceId = currentEntity.getImei();
        if (org.apache.commons.lang3.StringUtils.equals(request.getDeviceId(), currentDeviceId)) {
            LambdaQueryWrapper<FleetManagement> wrapper = new LambdaQueryWrapper<FleetManagement>();
            wrapper.select(FleetManagement::getImei);
            wrapper.ne(FleetManagement::getImei, (Object)request.getDeviceId());
            List<Object> deviceIdList = this.fleetManagementMapper.selectObjs(wrapper);
            if (deviceIdList.contains(request.getDeviceId())) {
                throw new AdminException.SameDeviceIdError();
            }
        }
        if ((entity = FleetManagementConverter.INSTANCE.updateRequest2Entity(request)).equals((Object)currentEntity)) {
            throw new GeneralException.DoNothing();
        }
        log.info("entity from update request: {}", (Object)entity);
        try {
            int effectRow = this.fleetManagementMapper.updateById(entity);
            return effectRow > 0;
        }
        catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new GeneralException.UnknownError();
        }
    }

    @Transactional
    public boolean doDeleteFleetManagement(String deviceId) {
        LambdaQueryWrapper<FleetManagement> wrapper = new LambdaQueryWrapper<FleetManagement>();
        wrapper.eq(FleetManagement::getImei, (Object)deviceId);
        try {
            int effectRow = this.fleetManagementMapper.delete((Wrapper)wrapper);
            return effectRow > 0;
        }
        catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new GeneralException.UnknownError();
        }
    }

    @Transactional
    public boolean doAddFleetManagement(AddCarMappingRequest request) {
        List<FleetManagement> fleetManagementList = this.fleetManagementMapper.selectList(null);
        for (FleetManagement each : fleetManagementList) {
            if (org.apache.commons.lang3.StringUtils.equals(each.getImei(), request.getDeviceId())) {
                throw new AdminException.SameDeviceIdError();
            }
            if (org.apache.commons.lang3.StringUtils.equals(each.getKt710Id(), request.getKt710Id())) {
                throw new AdminException.SameDeviceIdError();
            }
            if (!org.apache.commons.lang3.StringUtils.equals(each.getPlate(), request.getPlate())) continue;
            throw new AdminException.SamePlateError();
        }
        FleetManagement entity = FleetManagementConverter.INSTANCE.addRequest2Entity(request);
        log.info("adding new car mapping: {}", (Object)entity);
        try {
            int effectRow = this.fleetManagementMapper.insert(entity);
            return effectRow > 0;
        }
        catch (RuntimeException e) {
            log.error(e.getMessage());
            throw new GeneralException.UnknownError();
        }
    }

    public List<BrandModel> doGetAllModel() {
        return this.brandModelMapper.selectList(null);
    }

    public List<String> doGetAllDevice() {
        return this.fleetManagementMapper.selectList(null).stream().map(FleetManagement::getImei).toList();
    }

    public void doSwitchDeviceDebug(String deviceId, String operation) {
        if (org.apache.commons.lang3.StringUtils.equals(operation, "on")) {
            this.redisUtils.publishMessage("vaas:debug:device", "add:" + deviceId);
            log.info("enable device debug: {}", (Object)deviceId);
            this.scheduler.schedule(() -> {
                this.redisUtils.publishMessage("vaas:debug:device", "remove:" + deviceId);
                log.info("auto disable device debug after 10 min: {}", (Object)deviceId);
            }, 10L, TimeUnit.MINUTES);
        }
        if (org.apache.commons.lang3.StringUtils.equals(operation, "off")) {
            this.redisUtils.publishMessage("vaas:debug:device", "remove:" + deviceId);
            log.info("disable device debug: {}", (Object)deviceId);
        }
    }

    public GetHeartbeatResp doGetHeartbeatInfo() {
        return this.heartbeatComponent.getResp();
    }
}

