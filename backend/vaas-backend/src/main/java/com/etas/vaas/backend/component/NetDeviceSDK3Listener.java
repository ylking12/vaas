/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.api.IWeatherSensorService
 *  com.etas.vaas.backend.component.NetDeviceSDK3Listener
 *  com.etas.vaas.backend.component.NetDeviceSDK3Listener$1
 *  com.etas.vaas.backend.configuration.SensorConfig
 *  com.etas.vaas.backend.configuration.SensorConfig$SensorInfo
 *  com.etas.vaas.backend.configuration.SensorConfig$SensorType
 *  com.etas.vaas.backend.entity.SensorNodeData
 *  com.etas.vaas.backend.entity.SensorNodeDataEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 *  rk.netDevice.sdk.p3.AffectedParamItems
 *  rk.netDevice.sdk.p3.DataTransAck
 *  rk.netDevice.sdk.p3.DataTypes
 *  rk.netDevice.sdk.p3.IDataListener
 *  rk.netDevice.sdk.p3.LoginData
 *  rk.netDevice.sdk.p3.ParamIds
 *  rk.netDevice.sdk.p3.ParamItems
 *  rk.netDevice.sdk.p3.RealTimeData
 *  rk.netDevice.sdk.p3.StoreData
 *  rk.netDevice.sdk.p3.StoreDataAck
 *  rk.netDevice.sdk.p3.StoreDataCount
 *  rk.netDevice.sdk.p3.TelecontrolAck
 *  rk.netDevice.sdk.p3.TimingAck
 */
package com.etas.vaas.backend.component;

import com.etas.vaas.backend.api.IWeatherSensorService;
import com.etas.vaas.backend.component.NetDeviceSDK3Listener;
import com.etas.vaas.backend.configuration.SensorConfig;
import com.etas.vaas.backend.entity.SensorNodeData;
import com.etas.vaas.backend.entity.SensorNodeDataEntity;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rk.netDevice.sdk.p3.AffectedParamItems;
import rk.netDevice.sdk.p3.DataTransAck;
import rk.netDevice.sdk.p3.DataTypes;
import rk.netDevice.sdk.p3.IDataListener;
import rk.netDevice.sdk.p3.LoginData;
import rk.netDevice.sdk.p3.ParamIds;
import rk.netDevice.sdk.p3.ParamItems;
import rk.netDevice.sdk.p3.RealTimeData;
import rk.netDevice.sdk.p3.StoreData;
import rk.netDevice.sdk.p3.StoreDataAck;
import rk.netDevice.sdk.p3.StoreDataCount;
import rk.netDevice.sdk.p3.TelecontrolAck;
import rk.netDevice.sdk.p3.TimingAck;

@Component
public class NetDeviceSDK3Listener
implements IDataListener {
    private static final Logger log = LoggerFactory.getLogger(NetDeviceSDK3Listener.class);
    @Autowired
    private IWeatherSensorService weatherSensorService;
    @Autowired
    private SensorConfig sensorConfig;

    public NetDeviceSDK3Listener() {
        log.debug("NetDeviceSDK3Listener create");
    }

    public void receiveLoginData(LoginData loginData) {
        log.debug("receiveLoginData:{},{}", loginData.getDeviceAddr(), loginData.getSeqNum());
    }

    public void receiveTelecontrolAck(TelecontrolAck telecontrolAck) {
        log.debug("receiveTelecontrolAck:{},{}", telecontrolAck.getDeviceAddr(), telecontrolAck.getSeqNum());
    }

    public void receiveRealTimeData(RealTimeData realTimeData) {
        log.debug("receiveRealTimeData:{},{}", realTimeData.getDeviceAddr(), realTimeData.getSeqNum());
        SensorNodeDataEntity sensorNodeDataEntity = new SensorNodeDataEntity();
        // SDK3: setSensorId from deviceAddr
        sensorNodeDataEntity.setSensorId(realTimeData.getDeviceAddr());
        SensorConfig.SensorType sensorType = ((SensorConfig.SensorInfo)this.sensorConfig.getSensorIds().get(String.valueOf(sensorNodeDataEntity.getSensorId()))).getType();
        sensorNodeDataEntity.setSensorType(sensorType);
        realTimeData.getNodeDataList().forEach(nodeData -> {
            try {
                log.debug("nodedata:{},{},{}", nodeData.getData(), nodeData.getNodeId(), nodeData.toString());
                DataTypes type = nodeData.getDataType();
                Float analog1 = 0.0f;
                Float analog2 = 0.0f;
                Float floatV = 0.0f;
                Integer intV = -1;
                if (type != null) {
                    switch (type) {
                        case Float:
                            floatV = nodeData.getFloatData();
                            break;
                        case Integer:
                            intV = nodeData.getIntData();
                            break;
                        default:
                            log.debug("data {},{}", type, nodeData.getData());
                            break;
                    }
                }
                sensorNodeDataEntity.setCoordinateType((short)nodeData.getCoordinateType());
                sensorNodeDataEntity.setLatitude((float) nodeData.getLng());
                sensorNodeDataEntity.setLongitude((float) nodeData.getLng());
                SensorNodeData currNode = new SensorNodeData(nodeData.getNodeId(), Date.from(Instant.now()), analog1, analog2, floatV, intV.longValue());
                sensorNodeDataEntity.getNodeList().add(currNode);
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
        this.weatherSensorService.handlerSensorData(sensorNodeDataEntity);
    }

    public void receiveStoreData(StoreData storeData) {
        log.debug("receiveStoreData:{}", storeData.getDeviceAddr());
    }

    public void receiveStoreDataCount(StoreDataCount storeDataCount) {
        log.debug("receiveStoreDataCount:{}", storeDataCount.getDeviceAddr());
    }

    public void receiveStoreDataAck(StoreDataAck storeDataAck) {
        log.debug("receiveStoreDataAck:{}", storeDataAck.getDeviceAddr());
    }

    public void receiveTimingAck(TimingAck timingAck) {
        log.debug("receiveTimingAck:{}", timingAck.getDeviceAddr());
    }

    public void receiveParamIds(ParamIds paramIds) {
        log.debug("receiveParamIds:{}", paramIds.getDeviceAddr());
    }

    public void receiveParamItems(ParamItems paramItems) {
        log.debug("receiveParamItems:{}", paramItems.getDeviceAddr());
    }

    public void receiveAffectedParamItems(AffectedParamItems affectedParamItems) {
        log.debug("receiveAffectedParamItems:{}", affectedParamItems.getDeviceAddr());
    }

    public void receiveDataTransAck(DataTransAck dataTransAck) {
        log.debug("receiveDataTransAck:{}", dataTransAck.getDeviceAddr());
    }
}

