/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.mapper.BaseMapper
 *  org.apache.ibatis.annotations.Mapper
 *  org.apache.ibatis.annotations.Param
 *  org.apache.ibatis.annotations.Select
 */
package com.etas.vaas.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.etas.vaas.common.dto.report.EventByDateDTO;
import com.etas.vaas.common.dto.report.EventByTimeSlotDTO;
import com.etas.vaas.common.entity.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EventMapper
extends BaseMapper<Event> {
    @Select(value={"    SELECT\n        DATE(event_time) AS eventDate,\n        -- \u8f6c\u6362\u4e3a\u4e2d\u6587\u661f\u671f\uff081=\u5468\u65e5\uff0c2=\u5468\u4e00...7=\u5468\u516d\uff0c\u9700\u6620\u5c04\u8c03\u6574\uff09\n        ELT(WEEKDAY(DATE(event_time)) + 2, '\u5468\u65e5', '\u5468\u4e00', '\u5468\u4e8c', '\u5468\u4e09', '\u5468\u56db', '\u5468\u4e94', '\u5468\u516d') AS weekDay,\n        COUNT(*) AS eventCount,\n        SUM(CASE WHEN duplicated = 0 THEN 1 ELSE 0 END) AS validEventCount\n    FROM event\n    WHERE event_time BETWEEN #{startDateTime} AND #{endDateTime}\n    GROUP BY DATE(event_time)\n    ORDER BY DATE(event_time)\n"})
    public List<EventByDateDTO> selectEventByDate(@Param(value="startDateTime") LocalDateTime var1, @Param(value="endDateTime") LocalDateTime var2);

    @Select(value={"    SELECT\n        event_type AS eventType,\n        COUNT(*) AS eventCount\n    FROM event\n    WHERE event_time BETWEEN #{startDateTime} AND #{endDateTime}\n      AND duplicated = 0 -- \u53ea\u7edf\u8ba1\u6709\u6548\u4e8b\u4ef6\n    GROUP BY event_type\n"})
    public List<Map<String, Object>> selectEventTypeCountByWeek(@Param(value="startDateTime") LocalDateTime var1, @Param(value="endDateTime") LocalDateTime var2);

    @Select(value={"    SELECT\n        -- \u6309\u5c0f\u65f6\u5224\u65ad\u65f6\u6bb5\n        CASE\n            WHEN HOUR(event_time) BETWEEN 0 AND 6 THEN '00:00-07:00'\n            WHEN HOUR(event_time) BETWEEN 7 AND 8 THEN '07:00-09:00'\n            WHEN HOUR(event_time) BETWEEN 9 AND 11 THEN '09:00-12:00'\n            WHEN HOUR(event_time) BETWEEN 12 AND 13 THEN '12:00-14:00'\n            WHEN HOUR(event_time) BETWEEN 14 AND 16 THEN '14:00-17:00'\n            WHEN HOUR(event_time) BETWEEN 17 AND 18 THEN '17:00-19:00'\n            ELSE '19:00-24:00'\n        END AS timeSlot,\n        COUNT(*) AS eventCount\n    FROM event\n    WHERE event_time BETWEEN #{startDateTime} AND #{endDateTime}\n    GROUP BY timeSlot\n    ORDER BY\n        -- \u6309\u65f6\u6bb5\u987a\u5e8f\u6392\u5e8f\uff08\u907f\u514d\u6309\u5b57\u7b26\u4e32\u6392\u5e8f\u6df7\u4e71\uff09\n        CASE timeSlot\n            WHEN '00:00-07:00' THEN 1\n            WHEN '07:00-09:00' THEN 2\n            WHEN '09:00-12:00' THEN 3\n            WHEN '12:00-14:00' THEN 4\n            WHEN '14:00-17:00' THEN 5\n            WHEN '17:00-19:00' THEN 6\n            ELSE 7\n        END\n"})
    public List<EventByTimeSlotDTO> selectEventByTimeSlot(@Param(value="startDateTime") LocalDateTime var1, @Param(value="endDateTime") LocalDateTime var2);

    @Select(value={"    SELECT COUNT(*)\n    FROM event\n    WHERE event_time BETWEEN #{startDateTime} AND #{endDateTime}\n"})
    public Long selectTotalValidEventCount(@Param(value="startDateTime") LocalDateTime var1, @Param(value="endDateTime") LocalDateTime var2);

    @Select(value={"SELECT * FROM event WHERE road_name = #{roadName} and event_type =#{eventType} ORDER BY event_time DESC LIMIT 1"})
    public Optional<Event> getLatestPondingEventByRoadName(String var1, String var2);
}

