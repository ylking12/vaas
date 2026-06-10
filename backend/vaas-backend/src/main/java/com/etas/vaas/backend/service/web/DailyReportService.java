/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.conditions.Wrapper
 *  com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
 *  com.baomidou.mybatisplus.core.toolkit.CollectionUtils
 *  com.etas.vaas.backend.service.web.DailyReportService
 *  com.etas.vaas.common.dto.report.daily.BumpEventLevelDistDTO
 *  com.etas.vaas.common.dto.report.daily.CoreIndicatorDTO
 *  com.etas.vaas.common.dto.report.daily.EventDailyReport
 *  com.etas.vaas.common.dto.report.daily.HourlyTrendDTO
 *  com.etas.vaas.common.dto.report.daily.RoadTopDTO
 *  com.etas.vaas.common.entity.Event
 *  com.etas.vaas.common.mapper.EventMapper
 *  jakarta.annotation.Resource
 *  org.springframework.stereotype.Service
 */
package com.etas.vaas.backend.service.web;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.etas.vaas.common.dto.report.daily.BumpEventLevelDistDTO;
import com.etas.vaas.common.dto.report.daily.CoreIndicatorDTO;
import com.etas.vaas.common.dto.report.daily.EventDailyReport;
import com.etas.vaas.common.dto.report.daily.HourlyTrendDTO;
import com.etas.vaas.common.dto.report.daily.RoadTopDTO;
import com.etas.vaas.common.entity.Event;
import com.etas.vaas.common.mapper.EventMapper;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

@Service
public class DailyReportService {
    @Resource
    private EventMapper eventMapper;

    public EventDailyReport generateDailyReportData(LocalDate reportDate, int topRoadSize) {
        LocalDateTime startTime = LocalDateTime.of(reportDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(reportDate, LocalTime.MAX);
        QueryWrapper baseWrapper = (QueryWrapper)new QueryWrapper().between((Object)"event_time", (Object)startTime, endTime);
        List<Event> dailyEventList = this.eventMapper.selectList(baseWrapper);
        Long totalValidCount = dailyEventList.stream().filter(event -> event.getDuplicated() == false).count();
        EventDailyReport dailyReport = new EventDailyReport();
        dailyReport.setCoreIndicator(this.buildCoreIndicator(dailyEventList, startTime, endTime));
        dailyReport.setHourlyTrendList(this.buildHourlyTrend(dailyEventList));
        dailyReport.setRoadTopList(this.buildTopRoad(dailyEventList, topRoadSize));
        dailyReport.setRoadTopList(this.buildTopRoad(dailyEventList, topRoadSize));
        dailyReport.setEventTypeDistList(this.buildEventTypeDist(dailyEventList, totalValidCount));
        return dailyReport;
    }

    private CoreIndicatorDTO buildCoreIndicator(List<Event> eventList, LocalDateTime startTime, LocalDateTime endTime) {
        CoreIndicatorDTO indicator = new CoreIndicatorDTO();
        indicator.setTotalEventCount(Long.valueOf(eventList.size()));
        indicator.setSevereEventCount(Long.valueOf(eventList.stream().filter(e -> e.getLevel() != null && e.getLevel() == 7).count()));
        Long hasEventRoadCount = eventList.stream().map(Event::getRoadName).filter(Objects::nonNull).distinct().count();
        indicator.setUniqueRoadCount(hasEventRoadCount);
        return indicator;
    }

    private List<BumpEventLevelDistDTO> buildEventTypeDist(List<Event> eventList, Long totalValidCount) {
        if (totalValidCount == 0L) {
            return Collections.emptyList();
        }
        Map<String, Long> typeCountMap = eventList.stream().collect(Collectors.groupingBy(e -> e.getEventType().getTypeString(), Collectors.counting()));
        return typeCountMap.values().stream().map(aLong -> {
            BumpEventLevelDistDTO dto = new BumpEventLevelDistDTO();
            dto.setCount(aLong);
            Double proportion = new BigDecimal((long)aLong).divide(new BigDecimal(totalValidCount), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            dto.setProportion(proportion);
            return dto;
        }).sorted((a, b) -> b.getCount().compareTo(a.getCount())).collect(Collectors.toList());
    }

    private List<HourlyTrendDTO> buildHourlyTrend(List<Event> eventList) {
        Map<Integer, Long> hourlyCountMap = eventList.stream().filter(e -> e.getDuplicated() == false).collect(Collectors.groupingBy(e -> e.getEventTime().getHour(), Collectors.counting()));
        ArrayList<HourlyTrendDTO> trendList = new ArrayList<HourlyTrendDTO>();
        for (int hour = 0; hour < 24; ++hour) {
            HourlyTrendDTO dto = new HourlyTrendDTO();
            dto.setHour(Integer.valueOf(hour));
            dto.setHourRange(String.format("%02d:00-%02d:00", hour, hour + 1));
            dto.setEventCount(hourlyCountMap.getOrDefault(hour, 0L));
            trendList.add(dto);
        }
        return trendList;
    }

    private List<RoadTopDTO> buildTopRoad(List<Event> eventList, int maxSize) {
        Map<String, List<Event>> roadEventMap = eventList.stream().filter(e -> e.getDuplicated() == false && Objects.nonNull(e.getRoadName())).collect(Collectors.groupingBy(Event::getRoadName));
        if (CollectionUtils.isEmpty(roadEventMap)) {
            return Collections.emptyList();
        }
        List sortedRoadEntries = roadEventMap.entrySet().stream().sorted((a, b) -> Integer.compare(((List)b.getValue()).size(), ((List)a.getValue()).size())).limit(maxSize).toList();
        return IntStream.range(0, sortedRoadEntries.size()).mapToObj(index -> {
            Map.Entry entry = (Map.Entry)sortedRoadEntries.get(index);
            RoadTopDTO dto = new RoadTopDTO();
            dto.setRank(Integer.valueOf(index + 1));
            dto.setRoadName((String)entry.getKey());
            dto.setEventCount(Long.valueOf(((List)entry.getValue()).size()));
            return dto;
        }).collect(Collectors.toList());
    }
}

