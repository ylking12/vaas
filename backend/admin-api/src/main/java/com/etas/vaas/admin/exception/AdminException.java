package com.etas.vaas.admin.exception;

import com.etas.vaas.admin.ApiResponse;

/**
 * SOURCE: Decompiled from admin.jar
 * STATUS: Restored
 */
public class AdminException extends RuntimeException {
    protected final transient ApiResponse apiResponse;

    public AdminException() { super(); this.apiResponse = null; }
    public AdminException(String message) { super(message); this.apiResponse = null; }
    public AdminException(ApiResponse apiResponse) { super(); this.apiResponse = apiResponse; }
    public ApiResponse getApiResponse() { return this.apiResponse; }

    public static class SameDeviceIdError extends AdminException {
        public SameDeviceIdError() { super(); }
    }
    public static class SamePlateError extends AdminException {
        public SamePlateError() { super(); }
    }
    public static class NotFoundError extends AdminException {
        public NotFoundError() { super(); }
    }
}
