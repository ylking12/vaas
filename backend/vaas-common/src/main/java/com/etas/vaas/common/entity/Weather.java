/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.IdType
 *  com.baomidou.mybatisplus.annotation.TableField
 *  com.baomidou.mybatisplus.annotation.TableId
 *  com.baomidou.mybatisplus.annotation.TableName
 */
package com.etas.vaas.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName(value="weather")
public class Weather {
    @TableId(type=IdType.AUTO)
    private Integer id;
    @TableField(value="district_name")
    private String districtName;
    @TableField(value="district_id")
    private String districtId;
    @TableField(value="request_time")
    private LocalDateTime requestTime;
    @TableField(value="update_time")
    private LocalDateTime updateTime;
    @TableField(value="obs_time")
    private LocalDateTime obsTime;
    @TableField(value="temp")
    private Float temp;
    @TableField(value="feels_like")
    private Integer feelsLike;
    @TableField(value="icon")
    private String icon;
    @TableField(value="text")
    private String text;
    @TableField(value="wind_360")
    private Integer wind360;
    @TableField(value="wind_dir")
    private String windDir;
    @TableField(value="wind_scale")
    private Integer windScale;
    @TableField(value="wind_speed")
    private Integer windSpeed;
    @TableField(value="humidity")
    private Integer humidity;
    @TableField(value="precip")
    private Float precip;
    @TableField(value="pressure")
    private Float pressure;
    @TableField(value="vis")
    private Float vis;
    @TableField(value="cloud")
    private Float cloud;
    @TableField(value="dew")
    private Float dew;
    @TableField(value="sources")
    private String sources;
    @TableField(value="license")
    private String license;

    public Weather() {
    }

    public Integer getId() {
        return this.id;
    }

    public String getDistrictName() {
        return this.districtName;
    }

    public String getDistrictId() {
        return this.districtId;
    }

    public LocalDateTime getRequestTime() {
        return this.requestTime;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public LocalDateTime getObsTime() {
        return this.obsTime;
    }

    public Float getTemp() {
        return this.temp;
    }

    public Integer getFeelsLike() {
        return this.feelsLike;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }

    public Integer getWind360() {
        return this.wind360;
    }

    public String getWindDir() {
        return this.windDir;
    }

    public Integer getWindScale() {
        return this.windScale;
    }

    public Integer getWindSpeed() {
        return this.windSpeed;
    }

    public Integer getHumidity() {
        return this.humidity;
    }

    public Float getPrecip() {
        return this.precip;
    }

    public Float getPressure() {
        return this.pressure;
    }

    public Float getVis() {
        return this.vis;
    }

    public Float getCloud() {
        return this.cloud;
    }

    public Float getDew() {
        return this.dew;
    }

    public String getSources() {
        return this.sources;
    }

    public String getLicense() {
        return this.license;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public void setObsTime(LocalDateTime obsTime) {
        this.obsTime = obsTime;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }

    public void setFeelsLike(Integer feelsLike) {
        this.feelsLike = feelsLike;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setWind360(Integer wind360) {
        this.wind360 = wind360;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public void setWindScale(Integer windScale) {
        this.windScale = windScale;
    }

    public void setWindSpeed(Integer windSpeed) {
        this.windSpeed = windSpeed;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public void setPrecip(Float precip) {
        this.precip = precip;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }

    public void setVis(Float vis) {
        this.vis = vis;
    }

    public void setCloud(Float cloud) {
        this.cloud = cloud;
    }

    public void setDew(Float dew) {
        this.dew = dew;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Weather)) {
            return false;
        }
        Weather other = (Weather)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Integer this$id = this.getId();
        Integer other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Float this$temp = this.getTemp();
        Float other$temp = other.getTemp();
        if (this$temp == null ? other$temp != null : !((Object)this$temp).equals(other$temp)) {
            return false;
        }
        Integer this$feelsLike = this.getFeelsLike();
        Integer other$feelsLike = other.getFeelsLike();
        if (this$feelsLike == null ? other$feelsLike != null : !((Object)this$feelsLike).equals(other$feelsLike)) {
            return false;
        }
        Integer this$wind360 = this.getWind360();
        Integer other$wind360 = other.getWind360();
        if (this$wind360 == null ? other$wind360 != null : !((Object)this$wind360).equals(other$wind360)) {
            return false;
        }
        Integer this$windScale = this.getWindScale();
        Integer other$windScale = other.getWindScale();
        if (this$windScale == null ? other$windScale != null : !((Object)this$windScale).equals(other$windScale)) {
            return false;
        }
        Integer this$windSpeed = this.getWindSpeed();
        Integer other$windSpeed = other.getWindSpeed();
        if (this$windSpeed == null ? other$windSpeed != null : !((Object)this$windSpeed).equals(other$windSpeed)) {
            return false;
        }
        Integer this$humidity = this.getHumidity();
        Integer other$humidity = other.getHumidity();
        if (this$humidity == null ? other$humidity != null : !((Object)this$humidity).equals(other$humidity)) {
            return false;
        }
        Float this$precip = this.getPrecip();
        Float other$precip = other.getPrecip();
        if (this$precip == null ? other$precip != null : !((Object)this$precip).equals(other$precip)) {
            return false;
        }
        Float this$pressure = this.getPressure();
        Float other$pressure = other.getPressure();
        if (this$pressure == null ? other$pressure != null : !((Object)this$pressure).equals(other$pressure)) {
            return false;
        }
        Float this$vis = this.getVis();
        Float other$vis = other.getVis();
        if (this$vis == null ? other$vis != null : !((Object)this$vis).equals(other$vis)) {
            return false;
        }
        Float this$cloud = this.getCloud();
        Float other$cloud = other.getCloud();
        if (this$cloud == null ? other$cloud != null : !((Object)this$cloud).equals(other$cloud)) {
            return false;
        }
        Float this$dew = this.getDew();
        Float other$dew = other.getDew();
        if (this$dew == null ? other$dew != null : !((Object)this$dew).equals(other$dew)) {
            return false;
        }
        String this$districtName = this.getDistrictName();
        String other$districtName = other.getDistrictName();
        if (this$districtName == null ? other$districtName != null : !this$districtName.equals(other$districtName)) {
            return false;
        }
        String this$districtId = this.getDistrictId();
        String other$districtId = other.getDistrictId();
        if (this$districtId == null ? other$districtId != null : !this$districtId.equals(other$districtId)) {
            return false;
        }
        LocalDateTime this$requestTime = this.getRequestTime();
        LocalDateTime other$requestTime = other.getRequestTime();
        if (this$requestTime == null ? other$requestTime != null : !((Object)this$requestTime).equals(other$requestTime)) {
            return false;
        }
        LocalDateTime this$updateTime = this.getUpdateTime();
        LocalDateTime other$updateTime = other.getUpdateTime();
        if (this$updateTime == null ? other$updateTime != null : !((Object)this$updateTime).equals(other$updateTime)) {
            return false;
        }
        LocalDateTime this$obsTime = this.getObsTime();
        LocalDateTime other$obsTime = other.getObsTime();
        if (this$obsTime == null ? other$obsTime != null : !((Object)this$obsTime).equals(other$obsTime)) {
            return false;
        }
        String this$icon = this.getIcon();
        String other$icon = other.getIcon();
        if (this$icon == null ? other$icon != null : !this$icon.equals(other$icon)) {
            return false;
        }
        String this$text = this.getText();
        String other$text = other.getText();
        if (this$text == null ? other$text != null : !this$text.equals(other$text)) {
            return false;
        }
        String this$windDir = this.getWindDir();
        String other$windDir = other.getWindDir();
        if (this$windDir == null ? other$windDir != null : !this$windDir.equals(other$windDir)) {
            return false;
        }
        String this$sources = this.getSources();
        String other$sources = other.getSources();
        if (this$sources == null ? other$sources != null : !this$sources.equals(other$sources)) {
            return false;
        }
        String this$license = this.getLicense();
        String other$license = other.getLicense();
        return !(this$license == null ? other$license != null : !this$license.equals(other$license));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Weather;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Float $temp = this.getTemp();
        result = result * 59 + ($temp == null ? 43 : ((Object)$temp).hashCode());
        Integer $feelsLike = this.getFeelsLike();
        result = result * 59 + ($feelsLike == null ? 43 : ((Object)$feelsLike).hashCode());
        Integer $wind360 = this.getWind360();
        result = result * 59 + ($wind360 == null ? 43 : ((Object)$wind360).hashCode());
        Integer $windScale = this.getWindScale();
        result = result * 59 + ($windScale == null ? 43 : ((Object)$windScale).hashCode());
        Integer $windSpeed = this.getWindSpeed();
        result = result * 59 + ($windSpeed == null ? 43 : ((Object)$windSpeed).hashCode());
        Integer $humidity = this.getHumidity();
        result = result * 59 + ($humidity == null ? 43 : ((Object)$humidity).hashCode());
        Float $precip = this.getPrecip();
        result = result * 59 + ($precip == null ? 43 : ((Object)$precip).hashCode());
        Float $pressure = this.getPressure();
        result = result * 59 + ($pressure == null ? 43 : ((Object)$pressure).hashCode());
        Float $vis = this.getVis();
        result = result * 59 + ($vis == null ? 43 : ((Object)$vis).hashCode());
        Float $cloud = this.getCloud();
        result = result * 59 + ($cloud == null ? 43 : ((Object)$cloud).hashCode());
        Float $dew = this.getDew();
        result = result * 59 + ($dew == null ? 43 : ((Object)$dew).hashCode());
        String $districtName = this.getDistrictName();
        result = result * 59 + ($districtName == null ? 43 : $districtName.hashCode());
        String $districtId = this.getDistrictId();
        result = result * 59 + ($districtId == null ? 43 : $districtId.hashCode());
        LocalDateTime $requestTime = this.getRequestTime();
        result = result * 59 + ($requestTime == null ? 43 : ((Object)$requestTime).hashCode());
        LocalDateTime $updateTime = this.getUpdateTime();
        result = result * 59 + ($updateTime == null ? 43 : ((Object)$updateTime).hashCode());
        LocalDateTime $obsTime = this.getObsTime();
        result = result * 59 + ($obsTime == null ? 43 : ((Object)$obsTime).hashCode());
        String $icon = this.getIcon();
        result = result * 59 + ($icon == null ? 43 : $icon.hashCode());
        String $text = this.getText();
        result = result * 59 + ($text == null ? 43 : $text.hashCode());
        String $windDir = this.getWindDir();
        result = result * 59 + ($windDir == null ? 43 : $windDir.hashCode());
        String $sources = this.getSources();
        result = result * 59 + ($sources == null ? 43 : $sources.hashCode());
        String $license = this.getLicense();
        result = result * 59 + ($license == null ? 43 : $license.hashCode());
        return result;
    }

    public String toString() {
        return "Weather(id=" + this.getId() + ", districtName=" + this.getDistrictName() + ", districtId=" + this.getDistrictId() + ", requestTime=" + this.getRequestTime() + ", updateTime=" + this.getUpdateTime() + ", obsTime=" + this.getObsTime() + ", temp=" + this.getTemp() + ", feelsLike=" + this.getFeelsLike() + ", icon=" + this.getIcon() + ", text=" + this.getText() + ", wind360=" + this.getWind360() + ", windDir=" + this.getWindDir() + ", windScale=" + this.getWindScale() + ", windSpeed=" + this.getWindSpeed() + ", humidity=" + this.getHumidity() + ", precip=" + this.getPrecip() + ", pressure=" + this.getPressure() + ", vis=" + this.getVis() + ", cloud=" + this.getCloud() + ", dew=" + this.getDew() + ", sources=" + this.getSources() + ", license=" + this.getLicense() + ")";
    }
}

