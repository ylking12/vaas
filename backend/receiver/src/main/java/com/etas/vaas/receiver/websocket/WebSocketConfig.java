/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.receiver.websocket.WebSocketConfig
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.receiver.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="websocket")
public class WebSocketConfig {
    private String motionPath;
    private String locationPath;

    public String getMotionPath() {
        return this.motionPath;
    }

    public String getLocationPath() {
        return this.locationPath;
    }

    public void setMotionPath(String motionPath) {
        this.motionPath = motionPath;
    }

    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WebSocketConfig)) {
            return false;
        }
        WebSocketConfig other = (WebSocketConfig)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$motionPath = this.getMotionPath();
        String other$motionPath = other.getMotionPath();
        if (this$motionPath == null ? other$motionPath != null : !this$motionPath.equals(other$motionPath)) {
            return false;
        }
        String this$locationPath = this.getLocationPath();
        String other$locationPath = other.getLocationPath();
        return !(this$locationPath == null ? other$locationPath != null : !this$locationPath.equals(other$locationPath));
    }

    protected boolean canEqual(Object other) {
        return other instanceof WebSocketConfig;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $motionPath = this.getMotionPath();
        result = result * 59 + ($motionPath == null ? 43 : $motionPath.hashCode());
        String $locationPath = this.getLocationPath();
        result = result * 59 + ($locationPath == null ? 43 : $locationPath.hashCode());
        return result;
    }

    public String toString() {
        return "WebSocketConfig(motionPath=" + this.getMotionPath() + ", locationPath=" + this.getLocationPath() + ")";
    }

    public WebSocketConfig() {
    }
}

