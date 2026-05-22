package com.damanesign.sdk;

public class DamanesignException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    public DamanesignException(String message) {
        this(message, -1, null, null);
    }

    public DamanesignException(String message, Throwable cause) {
        this(message, -1, null, cause);
    }

    public DamanesignException(String message, int statusCode, String responseBody) {
        this(message, statusCode, responseBody, null);
    }

    public DamanesignException(String message, int statusCode, String responseBody, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
