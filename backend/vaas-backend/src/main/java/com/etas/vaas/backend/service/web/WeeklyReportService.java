/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.toolkit.CollectionUtils
 *  com.etas.vaas.backend.enumeration.RoadEventType
 *  com.etas.vaas.backend.service.web.WeeklyReportService
 *  com.etas.vaas.backend.service.web.WeeklyReportService$1
 *  com.etas.vaas.common.dto.report.EventByDateDTO
 *  com.etas.vaas.common.dto.report.EventByTimeSlotDTO
 *  com.etas.vaas.common.dto.report.EventTypeWeekChangeDTO
 *  com.etas.vaas.common.mapper.EventMapper
 *  jakarta.annotation.Resource
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.web;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.etas.vaas.backend.enumeration.RoadEventType;
import com.etas.vaas.backend.service.web.WeeklyReportService;
import com.etas.vaas.common.dto.report.EventByDateDTO;
import com.etas.vaas.common.dto.report.EventByTimeSlotDTO;
import com.etas.vaas.common.dto.report.EventTypeWeekChangeDTO;
import com.etas.vaas.common.mapper.EventMapper;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WeeklyReportService {
    @Resource
    private EventMapper eventMapper;

    public List<EventByDateDTO> getEventByDate(LocalDateTime weekStart, LocalDateTime weekEnd) {
        List dateList = this.eventMapper.selectEventByDate(weekStart, weekEnd);
        this.fillEmptyDate(weekStart, weekEnd, dateList);
        return dateList;
    }

    public List<EventTypeWeekChangeDTO> getEventTypeWeekChange(LocalDateTime currentWeekStart, LocalDateTime currentWeekEnd) {
        LocalDateTime lastWeekStart = currentWeekStart.minus(7L, ChronoUnit.DAYS);
        LocalDateTime lastWeekEnd = currentWeekEnd.minus(7L, ChronoUnit.DAYS);
        Map<String, Long> currentWeekMap = this.convertToEventTypeCountMap(this.eventMapper.selectEventTypeCountByWeek(currentWeekStart, currentWeekEnd));
        Map<String, Long> lastWeekMap = this.convertToEventTypeCountMap(this.eventMapper.selectEventTypeCountByWeek(lastWeekStart, lastWeekEnd));
        HashSet<String> allEventTypes = new HashSet();
        allEventTypes.addAll(currentWeekMap.keySet());
        allEventTypes.addAll(lastWeekMap.keySet());
        ArrayList<EventTypeWeekChangeDTO> changeList = new ArrayList<EventTypeWeekChangeDTO>();
        for (String eventType : allEventTypes) {
            Long currentCount = currentWeekMap.getOrDefault(eventType, 0L);
            Long lastCount = lastWeekMap.getOrDefault(eventType, 0L);
            Double changeRate = this.calculateChangeRate(currentCount, lastCount);
            EventTypeWeekChangeDTO dto = new EventTypeWeekChangeDTO();
            dto.setEventType(eventType);
            dto.setEventTypeCn(RoadEventType.getValueZhByValue((String)eventType));
            dto.setCurrentWeekCount(currentCount);
            dto.setLastWeekCount(lastCount);
            dto.setChangeRate(changeRate);
            changeList.add(dto);
        }
        changeList.sort((a, b) -> Objects.requireNonNull(b.getChangeRate()).compareTo(a.getChangeRate()));
        return changeList;
    }

    public List<EventByTimeSlotDTO> getEventByTimeSlot(LocalDateTime weekStart, LocalDateTime weekEnd) {
        List<EventByTimeSlotDTO> slotList = this.eventMapper.selectEventByTimeSlot(weekStart, weekEnd);
        Long totalValidCount = this.eventMapper.selectTotalValidEventCount(weekStart, weekEnd);
        if (totalValidCount == 0L) {
            slotList.forEach(slot -> slot.setProportion(Double.valueOf(0.0)));
            return slotList;
        }
        for (EventByTimeSlotDTO slot2 : slotList) {
            Double proportion = (double)slot2.getEventCount().longValue() / (double)totalValidCount.longValue() * 100.0;
            BigDecimal bd = new BigDecimal(proportion).setScale(2, RoundingMode.HALF_UP);
            slot2.setProportion(Double.valueOf(bd.doubleValue()));
        }
        return slotList;
    }

    private Map<String, Long> convertToEventTypeCountMap(List<Map<String, Object>> rawList) {
        if (CollectionUtils.isEmpty(rawList)) {
            return Collections.emptyMap();
        }
        return rawList.stream().collect(Collectors.toMap(map -> (String)map.get("eventType"), map -> (Long)map.get("eventCount"), (v1, v2) -> v1));
    }

    private Double calculateChangeRate(Long currentCount, Long lastCount) {
        if (lastCount == 0L) {
            return currentCount > 0L ? 100.0 : 0.0;
        }
        BigDecimal change = new BigDecimal(currentCount - lastCount);
        BigDecimal rate = change.divide(new BigDecimal(lastCount), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
        return rate.doubleValue();
    }

    private void fillEmptyDate(LocalDateTime weekStart, LocalDateTime weekEnd, List<EventByDateDTO> dateList) {
        HashSet<LocalDate> allDates = new HashSet<LocalDate>();
        LocalDate currentDate = weekStart.toLocalDate();
        LocalDate endDate = weekEnd.toLocalDate();
        while (!currentDate.isAfter(endDate)) {
            allDates.add(currentDate);
            currentDate = currentDate.plusDays(1L);
        }
        Set existingDates = dateList.stream().map(EventByDateDTO::getEventDate).collect(Collectors.toSet());
        for (LocalDate date : allDates) {
            if (existingDates.contains(date)) continue;
            EventByDateDTO emptyDto = new EventByDateDTO();
            emptyDto.setEventDate(date);
            String weekDay = switch (date.getDayOfWeek()) {
                default -> throw new IncompatibleClassChangeError();
                case MONDAY -> "\u5468\u4e00";
                case TUESDAY -> "\u5468\u4e8c";
                case WEDNESDAY -> "\u5468\u4e09";
                case THURSDAY -> "\u5468\u56db";
                case FRIDAY -> "\u5468\u4e94";
                case SATURDAY -> "\u5468\u516d";
                case SUNDAY -> "\u5468\u65e5";
            };
            emptyDto.setWeekDay(weekDay);
            emptyDto.setEventCount(Long.valueOf(0L));
            emptyDto.setValidEventCount(Long.valueOf(0L));
            dateList.add(emptyDto);
        }
        dateList.sort(Comparator.comparing(EventByDateDTO::getEventDate));
    }
}

