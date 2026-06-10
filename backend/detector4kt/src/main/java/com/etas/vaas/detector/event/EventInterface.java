/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.etas.vaas.common.dto.KtVehicleEvent
 *  com.etas.vaas.detector.entity.Frame
 *  com.etas.vaas.detector.event.EventInterface
 */
package com.etas.vaas.detector.event;

import com.etas.vaas.common.dto.KtVehicleEvent;
import com.etas.vaas.detector.entity.Frame;

public interface EventInterface {
    public KtVehicleEvent identify(Frame var1);
}

