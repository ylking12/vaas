/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.mapper.BaseMapper
 *  org.apache.ibatis.annotations.Mapper
 *  org.apache.ibatis.annotations.Param
 */
package com.etas.vaas.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.etas.vaas.common.entity.FleetManagement;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FleetManagementMapper
extends BaseMapper<FleetManagement> {
    public List<FleetManagement> selectFleetManagementByDataType(@Param(value="dataType") String var1);
}

