package com.etas.vaas.backend.dto.request;

/**
 * SOURCE: 新增（非原版还原产物） | STATUS: Added
 * 用途：按车按天统计点位数量的请求体。
 */
public class EventCountByVehicleRequest {
    /** 日期，ISO yyyy-MM-dd；为空时后端默认取今天 */
    private String date;

    public EventCountByVehicleRequest() {
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
