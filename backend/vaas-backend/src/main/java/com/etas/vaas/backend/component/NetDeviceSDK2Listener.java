/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.api.IWeatherSensorService
 *  com.etas.vaas.backend.component.NetDeviceSDK2Listener
 *  com.etas.vaas.backend.configuration.SensorConfig
 *  com.etas.vaas.backend.configuration.SensorConfig$SensorInfo
 *  com.etas.vaas.backend.entity.SensorNodeData
 *  com.etas.vaas.backend.entity.SensorNodeDataEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 *  rk.netDevice.sdk.p2.HeartbeatData
 *  rk.netDevice.sdk.p2.IDataListener
 *  rk.netDevice.sdk.p2.LoginData
 *  rk.netDevice.sdk.p2.ParamData
 *  rk.netDevice.sdk.p2.ParamIdsData
 *  rk.netDevice.sdk.p2.RealTimeData
 *  rk.netDevice.sdk.p2.StoreData
 *  rk.netDevice.sdk.p2.TelecontrolAck
 *  rk.netDevice.sdk.p2.TimmingAck
 *  rk.netDevice.sdk.p2.TransDataAck
 *  rk.netDevice.sdk.p2.WriteParamAck
 */
package com.etas.vaas.backend.component;

import com.etas.vaas.backend.api.IWeatherSensorService;
import com.etas.vaas.backend.configuration.SensorConfig;
import com.etas.vaas.backend.entity.SensorNodeData;
import com.etas.vaas.backend.entity.SensorNodeDataEntity;
import java.time.Instant;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rk.netDevice.sdk.p2.HeartbeatData;
import rk.netDevice.sdk.p2.IDataListener;
import rk.netDevice.sdk.p2.LoginData;
import rk.netDevice.sdk.p2.ParamData;
import rk.netDevice.sdk.p2.ParamIdsData;
import rk.netDevice.sdk.p2.RealTimeData;
import rk.netDevice.sdk.p2.StoreData;
import rk.netDevice.sdk.p2.TelecontrolAck;
import rk.netDevice.sdk.p2.TimmingAck;
import rk.netDevice.sdk.p2.TransDataAck;
import rk.netDevice.sdk.p2.WriteParamAck;

@Component
public class NetDeviceSDK2Listener {
    private static final Logger log = LoggerFactory.getLogger(NetDeviceSDK2Listener.class);
    @Autowired
    private SensorConfig sensorConfig;
    @Autowired
    private IWeatherSensorService weatherSensorService;

    public NetDeviceSDK2Listener() {
        log.debug("NetDeviceSDK2Listener create");
    }

    public void receiveRealtimeData(RealTimeData realTimeData) {
        log.debug("receiveRealTimeData:{},{}", realTimeData.getDeviceId(), realTimeData.getRelayStatus());
        SensorNodeDataEntity sensorNodeDataList = new SensorNodeDataEntity(realTimeData.getDeviceId(), ((SensorConfig.SensorInfo)this.sensorConfig.getSensorIds().get(String.valueOf(realTimeData.getDeviceId()))).getType(), realTimeData.getCoordinateType(), (float)realTimeData.getLat(), (float)realTimeData.getLng());
        realTimeData.getNodeList().forEach(nodeData -> {
            log.debug("nodedata:{},{},{},{},{}", nodeData.getNodeId(), nodeData.toString(), Float.valueOf(nodeData.getFloatValue()), Float.valueOf(nodeData.getTem()), Float.valueOf(nodeData.getHum()));
            Date recordTime = nodeData.getRecordTime() == null ? Date.from(Instant.now()) : nodeData.getRecordTime();
            SensorNodeData currNode = new SensorNodeData(nodeData.getNodeId(), recordTime, nodeData.getTem(), nodeData.getHum(), nodeData.getFloatValue(), nodeData.getUnSignedInt32Value());
            sensorNodeDataList.getNodeList().add(currNode);
        });
        this.weatherSensorService.handlerSensorData(sensorNodeDataList);
    }

    public void receiveLoginData(LoginData loginData) {
    }

    public void receiveStoreData(StoreData storeData) {
    }

    public void receiveTelecontrolAck(TelecontrolAck telecontrolAck) {
    }

    public void receiveTimmingAck(TimmingAck timmingAck) {
    }

    public void receiveParamIds(ParamIdsData paramIdsData) {
    }

    public void receiveParam(ParamData paramData) {
    }

    public void receiveWriteParamAck(WriteParamAck writeParamAck) {
    }

    public void receiveTransDataAck(TransDataAck transDataAck) {
    }

    public void receiveHeartbeatData(HeartbeatData heartbeatData) {
    }
}

