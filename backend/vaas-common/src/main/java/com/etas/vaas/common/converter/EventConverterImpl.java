/*
 * Decompiled with CFR 0.152.
 */
package com.etas.vaas.common.converter;

import com.etas.vaas.common.converter.EventConverter;
import com.etas.vaas.common.dao.VehicleEventDao;
import com.etas.vaas.common.dao.WeatherEventDao;
import com.etas.vaas.common.dto.KtVehicleEvent;
import com.etas.vaas.common.entity.Event;
import com.etas.vaas.common.enums.SourceType;

public class EventConverterImpl
implements EventConverter {
    @Override
    public Event weatherEventDao2Event(WeatherEventDao dao) {
        if (dao == null) {
            return null;
        }
        Event event = new Event();
        event.setReceivedTime(this.convertTimestamp(dao.getEventTimestamp()));
        event.setPerceptionTime(this.convertTimestamp(dao.getEventTimestamp()));
        event.setEventTime(this.convertTimestamp(dao.getEventTimestamp()));
        event.setEventId(dao.getEventId());
        event.setEventType(dao.getEventType());
        event.setSourceId(dao.getSourceId());
        event.setRoadName(dao.getRoadName());
        event.setLongitude(dao.getLongitude());
        event.setLatitude(dao.getLatitude());
        event.setSourceType(SourceType.WEATHER_SENSOR);
        event.setInArea(true);
        event.setDuplicated(false);
        return event;
    }

    @Override
    public Event vehicleEventDao2Event(VehicleEventDao dao) {
        if (dao == null) {
            return null;
        }
        Event event = new Event();
        event.setH3Hash(dao.getCellAddress());
        event.setSourceId(dao.getDeviceId());
        event.setReceivedTime(this.convertTimestamp(dao.getReceivedTimestamp()));
        event.setPerceptionTime(this.convertTimestamp(dao.getPerceptionTimestamp()));
        event.setEventTime(this.convertTimestamp(dao.getEventTimestamp()));
        event.setEventId(dao.getEventId());
        event.setEventType(dao.getEventType());
        event.setSourceType(dao.getSourceType());
        event.setRoadName(dao.getRoadName());
        event.setLongitude(dao.getLongitude());
        event.setLatitude(dao.getLatitude());
        event.setInArea(dao.getInArea());
        event.setDuplicated(dao.getDuplicated());
        event.setLevel(dao.getLevel());
        event.setSimulated(dao.getSimulated());
        return event;
    }

    @Override
    public VehicleEventDao ktEvent2VehicleEventDao(KtVehicleEvent kt) {
        if (kt == null) {
            return null;
        }
        VehicleEventDao vehicleEventDao = new VehicleEventDao();
        vehicleEventDao.setEventId(kt.getEventId());
        vehicleEventDao.setEventType(kt.getEventType());
        vehicleEventDao.setDeviceId(kt.getDeviceId());
        vehicleEventDao.setSourceType(kt.getSourceType());
        vehicleEventDao.setRoadName(kt.getRoadName());
        vehicleEventDao.setLongitude(kt.getLongitude());
        vehicleEventDao.setLatitude(kt.getLatitude());
        vehicleEventDao.setInArea(kt.getInArea());
        vehicleEventDao.setEventTimestamp(kt.getEventTimestamp());
        vehicleEventDao.setReceivedTimestamp(kt.getReceivedTimestamp());
        vehicleEventDao.setPerceptionTimestamp(kt.getPerceptionTimestamp());
        vehicleEventDao.setDuplicated(kt.getDuplicated());
        vehicleEventDao.setLevel(kt.getLevel());
        vehicleEventDao.setStatus(kt.getStatus());
        vehicleEventDao.setCellAddress(kt.getCellAddress());
        vehicleEventDao.setSimulated(kt.getSimulated());
        return vehicleEventDao;
    }
}

