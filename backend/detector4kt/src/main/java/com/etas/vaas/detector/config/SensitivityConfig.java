/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.detector.config.SensitivityConfig
 *  com.etas.vaas.detector.config.SensitivityConfig$Kt
 *  jakarta.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.context.annotation.Configuration
 */
package com.etas.vaas.detector.config;

import com.etas.vaas.detector.config.SensitivityConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="algorithm")
public class SensitivityConfig {
    private static final Logger log = LoggerFactory.getLogger(SensitivityConfig.class);
    private Kt kt;

    @PostConstruct
    void init() {
        log.info("current kt config\uff1b {}", (Object)this.kt);
    }

    public SensitivityConfig() {
    }

    public Kt getKt() {
        return this.kt;
    }

    public void setKt(Kt kt) {
        this.kt = kt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SensitivityConfig)) {
            return false;
        }
        SensitivityConfig other = (SensitivityConfig)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Kt this$kt = this.getKt();
        Kt other$kt = other.getKt();
        return !(this$kt == null ? other$kt != null : !this$kt.equals(other$kt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SensitivityConfig;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Kt $kt = this.getKt();
        result = result * 59 + ($kt == null ? 43 : $kt.hashCode());
        return result;
    }

    public String toString() {
        return "SensitivityConfig(kt=" + this.getKt() + ")";
    }

    public static class Kt {
        private BumpConfig bump;
        private SlipConfig slip;
        public BumpConfig getBump() { return bump; }
        public void setBump(BumpConfig bump) { this.bump = bump; }
        public SlipConfig getSlip() { return slip; }
        public void setSlip(SlipConfig slip) { this.slip = slip; }
    }
    public static class BumpConfig {
        private double steerRatioDiffLv1;
        private double steerRatioDiffLv2;
        private double meanBreakPressureThreshold;
        private double sumSpeedRatioThreshold;
        private double correlationThreshold;
        private double meanSpeedThreshold;
        public double getSteerRatioDiffLv1() { return steerRatioDiffLv1; }
        public void setSteerRatioDiffLv1(double v) { this.steerRatioDiffLv1 = v; }
        public double getSteerRatioDiffLv2() { return steerRatioDiffLv2; }
        public void setSteerRatioDiffLv2(double v) { this.steerRatioDiffLv2 = v; }
        public double getMeanBreakPressureThreshold() { return meanBreakPressureThreshold; }
        public void setMeanBreakPressureThreshold(double v) { this.meanBreakPressureThreshold = v; }
        public double getSumSpeedRatioThreshold() { return sumSpeedRatioThreshold; }
        public void setSumSpeedRatioThreshold(double v) { this.sumSpeedRatioThreshold = v; }
        public double getCorrelationThreshold() { return correlationThreshold; }
        public void setCorrelationThreshold(double v) { this.correlationThreshold = v; }
        public double getMeanSpeedThreshold() { return meanSpeedThreshold; }
        public void setMeanSpeedThreshold(double v) { this.meanSpeedThreshold = v; }
    }
    public static class SlipConfig {
        private double speedThreshold;
        private double muThreshold;
        public double getSpeedThreshold() { return speedThreshold; }
        public void setSpeedThreshold(double v) { this.speedThreshold = v; }
        public double getMuThreshold() { return muThreshold; }
        public void setMuThreshold(double v) { this.muThreshold = v; }
    }
}

