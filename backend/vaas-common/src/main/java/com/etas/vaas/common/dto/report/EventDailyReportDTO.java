/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 */
package com.etas.vaas.common.dto.report;

import com.etas.vaas.common.dto.report.RoadEventTopDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EventDailyReportDTO {
    private LocalDate reportDate;
    private String timeRange;
    private Long totalEventCount;
    private Long urgentEventCount;
    private Double coverageRate;
    private Map<String, Long> eventTypeDist;
    private Map<String, Long> sourceTypeDist;
    private List<RoadEventTopDTO> roadEventTop5;

    public EventDailyReportDTO() {
    }

    public LocalDate getReportDate() {
        return this.reportDate;
    }

    public String getTimeRange() {
        return this.timeRange;
    }

    public Long getTotalEventCount() {
        return this.totalEventCount;
    }

    public Long getUrgentEventCount() {
        return this.urgentEventCount;
    }

    public Double getCoverageRate() {
        return this.coverageRate;
    }

    public Map<String, Long> getEventTypeDist() {
        return this.eventTypeDist;
    }

    public Map<String, Long> getSourceTypeDist() {
        return this.sourceTypeDist;
    }

    public List<RoadEventTopDTO> getRoadEventTop5() {
        return this.roadEventTop5;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public void setTotalEventCount(Long totalEventCount) {
        this.totalEventCount = totalEventCount;
    }

    public void setUrgentEventCount(Long urgentEventCount) {
        this.urgentEventCount = urgentEventCount;
    }

    public void setCoverageRate(Double coverageRate) {
        this.coverageRate = coverageRate;
    }

    public void setEventTypeDist(Map<String, Long> eventTypeDist) {
        this.eventTypeDist = eventTypeDist;
    }

    public void setSourceTypeDist(Map<String, Long> sourceTypeDist) {
        this.sourceTypeDist = sourceTypeDist;
    }

    public void setRoadEventTop5(List<RoadEventTopDTO> roadEventTop5) {
        this.roadEventTop5 = roadEventTop5;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventDailyReportDTO)) {
            return false;
        }
        EventDailyReportDTO other = (EventDailyReportDTO)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$totalEventCount = this.getTotalEventCount();
        Long other$totalEventCount = other.getTotalEventCount();
        if (this$totalEventCount == null ? other$totalEventCount != null : !((Object)this$totalEventCount).equals(other$totalEventCount)) {
            return false;
        }
        Long this$urgentEventCount = this.getUrgentEventCount();
        Long other$urgentEventCount = other.getUrgentEventCount();
        if (this$urgentEventCount == null ? other$urgentEventCount != null : !((Object)this$urgentEventCount).equals(other$urgentEventCount)) {
            return false;
        }
        Double this$coverageRate = this.getCoverageRate();
        Double other$coverageRate = other.getCoverageRate();
        if (this$coverageRate == null ? other$coverageRate != null : !((Object)this$coverageRate).equals(other$coverageRate)) {
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
        Map<String, Long> this$eventTypeDist = this.getEventTypeDist();
        Map<String, Long> other$eventTypeDist = other.getEventTypeDist();
        if (this$eventTypeDist == null ? other$eventTypeDist != null : !((Object)this$eventTypeDist).equals(other$eventTypeDist)) {
            return false;
        }
        Map<String, Long> this$sourceTypeDist = this.getSourceTypeDist();
        Map<String, Long> other$sourceTypeDist = other.getSourceTypeDist();
        if (this$sourceTypeDist == null ? other$sourceTypeDist != null : !((Object)this$sourceTypeDist).equals(other$sourceTypeDist)) {
            return false;
        }
        List<RoadEventTopDTO> this$roadEventTop5 = this.getRoadEventTop5();
        List<RoadEventTopDTO> other$roadEventTop5 = other.getRoadEventTop5();
        return !(this$roadEventTop5 == null ? other$roadEventTop5 != null : !((Object)this$roadEventTop5).equals(other$roadEventTop5));
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventDailyReportDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $totalEventCount = this.getTotalEventCount();
        result = result * 59 + ($totalEventCount == null ? 43 : ((Object)$totalEventCount).hashCode());
        Long $urgentEventCount = this.getUrgentEventCount();
        result = result * 59 + ($urgentEventCount == null ? 43 : ((Object)$urgentEventCount).hashCode());
        Double $coverageRate = this.getCoverageRate();
        result = result * 59 + ($coverageRate == null ? 43 : ((Object)$coverageRate).hashCode());
        LocalDate $reportDate = this.getReportDate();
        result = result * 59 + ($reportDate == null ? 43 : ((Object)$reportDate).hashCode());
        String $timeRange = this.getTimeRange();
        result = result * 59 + ($timeRange == null ? 43 : $timeRange.hashCode());
        Map<String, Long> $eventTypeDist = this.getEventTypeDist();
        result = result * 59 + ($eventTypeDist == null ? 43 : ((Object)$eventTypeDist).hashCode());
        Map<String, Long> $sourceTypeDist = this.getSourceTypeDist();
        result = result * 59 + ($sourceTypeDist == null ? 43 : ((Object)$sourceTypeDist).hashCode());
        List<RoadEventTopDTO> $roadEventTop5 = this.getRoadEventTop5();
        result = result * 59 + ($roadEventTop5 == null ? 43 : ((Object)$roadEventTop5).hashCode());
        return result;
    }

    public String toString() {
        return "EventDailyReportDTO(reportDate=" + this.getReportDate() + ", timeRange=" + this.getTimeRange() + ", totalEventCount=" + this.getTotalEventCount() + ", urgentEventCount=" + this.getUrgentEventCount() + ", coverageRate=" + this.getCoverageRate() + ", eventTypeDist=" + this.getEventTypeDist() + ", sourceTypeDist=" + this.getSourceTypeDist() + ", roadEventTop5=" + this.getRoadEventTop5() + ")";
    }
}

