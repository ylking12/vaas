package com.etas.vaas.backend.dto.response;

/**
 * SOURCE: 新增（非原版还原产物） | STATUS: Added
 * 用途：每辆采集车当天上报的颠簸/湿滑点位数量统计响应。
 *      积水(PONDING)由路测气象站上报、非采集车，故不含积水字段。
 */
public class VehicleEventCountResponse {
    /** 脱敏车牌 */
    private String plate;
    /** 颠簸点位数 */
    private Long bumpCount;
    /** 湿滑点位数 */
    private Long slipCount;
    /** 合计（颠簸+湿滑），用于排序 */
    private Long totalCount;

    public VehicleEventCountResponse() {
    }

    public String getPlate() {
        return this.plate;
    }

    public Long getBumpCount() {
        return this.bumpCount;
    }

    public Long getSlipCount() {
        return this.slipCount;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setBumpCount(Long bumpCount) {
        this.bumpCount = bumpCount;
    }

    public void setSlipCount(Long slipCount) {
        this.slipCount = slipCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
