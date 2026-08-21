/*
 * Decompiled with CFR 0.152.
 */
package com.etas.vaas.detector.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "algorithm")
public class SensitivityConfig {
    private static final Logger log = LoggerFactory.getLogger(SensitivityConfig.class);
    private Kt kt;

    @PostConstruct
    void init() {
        log.info("current kt config； {}", this.kt);
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
        SensitivityConfig other = (SensitivityConfig) o;
        if (!other.canEqual(this)) {
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
        return result * PRIME + ($kt == null ? 43 : $kt.hashCode());
    }

    public String toString() {
        return "SensitivityConfig(kt=" + this.getKt() + ")";
    }

    public static class Kt {
        private Bump bump;
        private Slip slip;

        public Bump getBump() {
            return this.bump;
        }

        public void setBump(Bump bump) {
            this.bump = bump;
        }

        public Slip getSlip() {
            return this.slip;
        }

        public void setSlip(Slip slip) {
            this.slip = slip;
        }

        public static class Bump {
            private Float steerRatioDiffLv1;
            private Float steerRatioDiffLv2;
            private Integer meanBreakPressureThreshold;
            private Integer sumSpeedRatioThreshold;
            private Float correlationThreshold;
            private Float meanSpeedThreshold;

            public Float getSteerRatioDiffLv1() {
                return this.steerRatioDiffLv1;
            }

            public void setSteerRatioDiffLv1(Float steerRatioDiffLv1) {
                this.steerRatioDiffLv1 = steerRatioDiffLv1;
            }

            public Float getSteerRatioDiffLv2() {
                return this.steerRatioDiffLv2;
            }

            public void setSteerRatioDiffLv2(Float steerRatioDiffLv2) {
                this.steerRatioDiffLv2 = steerRatioDiffLv2;
            }

            public Integer getMeanBreakPressureThreshold() {
                return this.meanBreakPressureThreshold;
            }

            public void setMeanBreakPressureThreshold(Integer meanBreakPressureThreshold) {
                this.meanBreakPressureThreshold = meanBreakPressureThreshold;
            }

            public Integer getSumSpeedRatioThreshold() {
                return this.sumSpeedRatioThreshold;
            }

            public void setSumSpeedRatioThreshold(Integer sumSpeedRatioThreshold) {
                this.sumSpeedRatioThreshold = sumSpeedRatioThreshold;
            }

            public Float getCorrelationThreshold() {
                return this.correlationThreshold;
            }

            public void setCorrelationThreshold(Float correlationThreshold) {
                this.correlationThreshold = correlationThreshold;
            }

            public Float getMeanSpeedThreshold() {
                return this.meanSpeedThreshold;
            }

            public void setMeanSpeedThreshold(Float meanSpeedThreshold) {
                this.meanSpeedThreshold = meanSpeedThreshold;
            }
        }

        public static class Slip {
            private Float muThreshold;

            public Float getMuThreshold() {
                return this.muThreshold;
            }

            public void setMuThreshold(Float muThreshold) {
                this.muThreshold = muThreshold;
            }
        }
    }
}
