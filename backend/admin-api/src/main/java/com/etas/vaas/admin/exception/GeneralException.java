package com.etas.vaas.admin.exception;

import com.etas.vaas.admin.ApiResponse;

public class GeneralException extends RuntimeException {
    protected final transient ApiResponse apiResponse;

    public GeneralException() { super(); this.apiResponse = null; }
    public GeneralException(String message) { super(message); this.apiResponse = null; }
    public GeneralException(ApiResponse apiResponse) { super(); this.apiResponse = apiResponse; }
    public ApiResponse getApiResponse() { return this.apiResponse; }

    public static class DoNothing extends GeneralException {
        public DoNothing() { super(); }
    }
    public static class UnknownError extends GeneralException {
        public UnknownError() { super(); }
    }
}
