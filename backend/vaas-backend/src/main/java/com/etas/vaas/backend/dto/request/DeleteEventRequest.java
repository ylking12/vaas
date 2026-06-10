/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.backend.dto.request.DeleteEventRequest
 *  com.etas.vaas.common.enums.EventType
 */
package com.etas.vaas.backend.dto.request;

import com.etas.vaas.common.enums.EventType;

public class DeleteEventRequest {
    private EventType eventType;
    private String eventId;

    public EventType getEventType() {
        return this.eventType;
    }

    public String getEventId() {
        return this.eventId;
    }
}

