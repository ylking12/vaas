/** SOURCE: Decompiled from receiver.jar | ORIGINAL: com.etas.vaas.receiver.config.DumpConfig | STATUS: Restored */
package com.etas.vaas.receiver.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dump")
public class DumpConfig {
    private SubDump coordinate;
    private SubDump kt;
    private SubDump motion;

    public SubDump getCoordinate() {
        return coordinate;
    }

    public SubDump getKt() {
        return kt;
    }

    public SubDump getMotion() {
        return motion;
    }

    public void setCoordinate(SubDump coordinate) {
        this.coordinate = coordinate;
    }

    public void setKt(SubDump kt) {
        this.kt = kt;
    }

    public void setMotion(SubDump motion) {
        this.motion = motion;
    }

    public static class SubDump {
        private boolean enable;
        private List<String> device;

        public boolean isEnable() {
            return enable;
        }

        public List<String> getDevice() {
            return device;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public void setDevice(List<String> device) {
            this.device = device;
        }
    }
}
