/*
 * Decompiled with CFR 0.152.
 */
package com.etas.vaas.common.exception;

public class EventError
extends RuntimeException {
    public EventError(String msg) {
        super(msg);
    }

    public static class OutOfArea
    extends EventError {
        public OutOfArea(String msg) {
            super(msg);
        }
    }

    public static class ClosestLocationNotFoundError
    extends EventError {
        public ClosestLocationNotFoundError(String msg) {
            super(msg);
        }
    }

    public static class NoGpsError
    extends EventError {
        public NoGpsError(String msg) {
            super(msg);
        }
    }
}

