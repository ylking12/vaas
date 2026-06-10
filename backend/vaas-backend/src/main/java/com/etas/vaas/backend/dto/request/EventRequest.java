/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.request.EventRequest
 */
package com.etas.vaas.backend.dto.request;


public class EventRequest {
    private int hour;

    public EventRequest() {
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventRequest)) {
            return false;
        }
        EventRequest other = (EventRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.getHour() == other.getHour();
    }

    protected boolean canEqual(Object other) {
        return other instanceof EventRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getHour();
        return result;
    }

    public String toString() {
        return "EventRequest(hour=" + this.getHour() + ")";
    }
}

