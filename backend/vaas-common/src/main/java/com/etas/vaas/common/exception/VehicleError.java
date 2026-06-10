/*
 * Decompiled with CFR 0.152.
 */
package com.etas.vaas.common.exception;

public class VehicleError
extends RuntimeException {
    public VehicleError(String msg) {
        super(msg);
    }

    public static class NoDeviceIdError
    extends VehicleError {
        public NoDeviceIdError(String msg) {
            super(msg);
        }
    }
}

