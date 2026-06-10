/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report.daily;

import com.etas.vaas.common.dto.report.daily.BumpEventLevelDistDTO;
import com.etas.vaas.common.dto.report.daily.CoreIndicatorDTO;
import com.etas.vaas.common.dto.report.daily.HourlyTrendDTO;
import com.etas.vaas.common.dto.report.daily.RoadTopDTO;
import java.time.LocalDate;
import java.util.List;

public class EventDailyReport {
    private LocalDate reportDate;
    private String timeRange = "00:00:00 ~ 23:59:59";
    private CoreIndicatorDTO coreIndicator;
    private List<BumpEventLevelDistDTO> eventTypeDistList;
    private List<HourlyTrendDTO> hourlyTrendList;
    private List<RoadTopDTO> roadTopList;

    public EventDailyReport() {
    }

    public LocalDate getReportDate() {
        return this.reportDate;
    }

    public String getTimeRange() {
        return this.timeRange;
    }

    public CoreIndicatorDTO getCoreIndicator() {
        return this.coreIndicator;
    }

    public List<BumpEventLevelDistDTO> getEventTypeDistList() {
        return this.eventTypeDistList;
    }

    public List<HourlyTrendDTO> getHourlyTrendList() {
        return this.hourlyTrendList;
    }

    public List<RoadTopDTO> getRoadTopList() {
        return this.roadTopList;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public void setCoreIndicator(CoreIndicatorDTO coreIndicator) {
        this.coreIndicator = coreIndicator;
    }

    public void setEventTypeDistList(List<BumpEventLevelDistDTO> eventTypeDistList) {
        this.eventTypeDistList = eventTypeDistList;
    }

    public void setHourlyTrendList(List<HourlyTrendDTO> hourlyTrendList) {
        this.hourlyTrendList = hourlyTrendList;
    }

    public void setRoadTopList(List<RoadTopDTO> roadTopList) {
        this.roadTopList = roadTopList;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventDailyReport)) {
            return false;
        }
        EventDailyReport other = (EventDailyReport)o;
        if (!other.canEqual(this)) {
            return false;
        }
        LocalDate this$reportDate = this.getReportDate();
        LocalDate other$reportDate = other.getReportDate();
        if (this$reportDate == null ? other$reportDate != null : !((Object)this$reportDate).equals(other$reportDate)) {
            return false;
        }
        String this$timeRange = this.getTimeRange();
        String other$timeRange = other.getTimeRange();
        if (this$timeRange == null ? other$timeRange != null : !this$timeRange.equals(other$timeRange)) {
            return false;
        }
        CoreIndicatorDTO this$coreIndicator = this.getCoreIndicator();
        CoreIndicatorDTO other$coreIndicator = other.getCoreIndicator();
        if (this$coreIndicator == null ? other$coreIndicator != null : !((Object)this$coreIndicator).equals(other$coreIndicator)) {
            return false;
        }
        List<BumpEventLevelDistDTO> this$eventTypeDistList = this.getEventTypeDistList();
        List<BumpEventLevelDistDTO> other$eventTypeDistList = other.getEventTypeDistList();
        if (this$eventTypeDistList == null ? other$eventTypeDistList != null : !((Object)this$eventTypeDistList).equals(other$eventTypeDistList)) {
            return false;
        }
        List<HourlyTrendDTO> this$hourlyTrendList = this.getHourlyTrendList();
        List<HourlyTrendDTO> other$hourlyTrendList = other.getHourlyTrendList();
        if (this$hourlyTrendList == null ? other$hourlyTrendList != null : !((Object)this$hourlyTrendList).equals(other$hourlyTrendList)) {
            return false;
        }
        List<RoadTopDTO> this$roadTopList = this.getRoadTopList();
        List<RoadTopDTO> other$roadTopList = other.getRoadTopList();
        return !(this$roadTopList == null ? other$roadTopList != null : !((Object)this$roadTopList).equals(other$roadTopList));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventDailyReport;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        LocalDate $reportDate = this.getReportDate();
        result = result * 59 + ($reportDate == null ? 43 : ((Object)$reportDate).hashCode());
        String $timeRange = this.getTimeRange();
        result = result * 59 + ($timeRange == null ? 43 : $timeRange.hashCode());
        CoreIndicatorDTO $coreIndicator = this.getCoreIndicator();
        result = result * 59 + ($coreIndicator == null ? 43 : ((Object)$coreIndicator).hashCode());
        List<BumpEventLevelDistDTO> $eventTypeDistList = this.getEventTypeDistList();
        result = result * 59 + ($eventTypeDistList == null ? 43 : ((Object)$eventTypeDistList).hashCode());
        List<HourlyTrendDTO> $hourlyTrendList = this.getHourlyTrendList();
        result = result * 59 + ($hourlyTrendList == null ? 43 : ((Object)$hourlyTrendList).hashCode());
        List<RoadTopDTO> $roadTopList = this.getRoadTopList();
        result = result * 59 + ($roadTopList == null ? 43 : ((Object)$roadTopList).hashCode());
        return result;
    }

    public String toString() {
        return "EventDailyReport(reportDate=" + this.getReportDate() + ", timeRange=" + this.getTimeRange() + ", coreIndicator=" + this.getCoreIndicator() + ", eventTypeDistList=" + this.getEventTypeDistList() + ", hourlyTrendList=" + this.getHourlyTrendList() + ", roadTopList=" + this.getRoadTopList() + ")";
    }
}

