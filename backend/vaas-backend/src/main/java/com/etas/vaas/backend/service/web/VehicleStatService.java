package com.etas.vaas.backend.service.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.etas.vaas.backend.dto.response.VehicleEventCountResponse;
import com.etas.vaas.common.component.FleetManagementComponent;
import com.etas.vaas.common.entity.Event;
import com.etas.vaas.common.entity.FleetManagement;
import com.etas.vaas.common.enums.EventType;
import com.etas.vaas.common.enums.SourceType;
import com.etas.vaas.common.mapper.EventMapper;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * SOURCE: 新增（非原版还原产物） | STATUS: Added
 * 用途：按采集车(imei)按天统计颠簸/湿滑点位数量，供大屏"采集车上报排行"展示。
 * 机制：
 *   - 按 event_time 日期范围查 event 表，只取采集车源(kt710/motionSensor)，排除气象站
 *   - 按 source_id(imei) 分组，内层按 eventType 计数
 *   - 经 FleetManagementComponent 映射 imei->车牌并脱敏
 *   - 复用 DailyReportService 的 QueryWrapper.between + stream groupingBy 范式
 */
@Service
public class VehicleStatService {
    @Resource
    private EventMapper eventMapper;
    @Resource
    private FleetManagementComponent fleetManagementComponent;

    public List<VehicleEventCountResponse> getEventCountByVehicle(String dateStr) {
        LocalDate reportDate;
        if (StringUtils.isBlank(dateStr)) {
            reportDate = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        } else {
            reportDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        }
        LocalDateTime startTime = LocalDateTime.of(reportDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(reportDate, LocalTime.MAX);

        QueryWrapper<Event> wrapper = new QueryWrapper<>();
        wrapper.between("event_time", startTime, endTime)
                .in("source_type", SourceType.KT.getTypeString(), SourceType.MOTION_SENSOR.getTypeString());
        List<Event> eventList = this.eventMapper.selectList(wrapper);

        Map<String, List<Event>> byVehicle = eventList.stream()
                .filter(e -> Objects.nonNull(e.getSourceId()))
                .collect(Collectors.groupingBy(Event::getSourceId));

        List<VehicleEventCountResponse> result = new ArrayList<>();
        for (Map.Entry<String, List<Event>> entry : byVehicle.entrySet()) {
            String imei = entry.getKey();
            Map<EventType, Long> typeCount = entry.getValue().stream()
                    .filter(e -> Objects.nonNull(e.getEventType()))
                    .collect(Collectors.groupingBy(Event::getEventType, Collectors.counting()));
            long bump = typeCount.getOrDefault(EventType.BUMP, 0L);
            long slip = typeCount.getOrDefault(EventType.SLIP, 0L);

            VehicleEventCountResponse resp = new VehicleEventCountResponse();
            resp.setBumpCount(bump);
            resp.setSlipCount(slip);
            resp.setTotalCount(bump + slip);
            FleetManagement fm = this.fleetManagementComponent.getDeviceId2CarMap().get(imei);
            resp.setPlate(maskPlate(fm));
            result.add(resp);
        }
        result.sort((a, b) -> Long.compare(b.getTotalCount(), a.getTotalCount()));
        return result;
    }

    /** 车牌脱敏：保留前缀，末 2 位用 ** 替换（参考 EventService.getAlarmList 的脱敏风格） */
    private String maskPlate(FleetManagement fm) {
        if (fm == null || StringUtils.isBlank(fm.getPlate())) {
            return "未知车辆";
        }
        String plate = fm.getPlate();
        if (plate.length() <= 2) {
            return plate + "**";
        }
        return plate.substring(0, plate.length() - 2) + "**";
    }
}
