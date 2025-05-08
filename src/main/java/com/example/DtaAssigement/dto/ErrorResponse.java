package com.example.DtaAssigement.dto;


import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private String path;
    private String exception;
    private String method;
    private String stackTrace;

    public ErrorResponse(LocalDateTime timestamp, String error, String message,
                         String path, String exception, String method, String stackTrace) {
        this.timestamp = timestamp;
        this.error = error;
        this.message = message;
        this.path = path;
        this.exception = exception;
        this.method = method;
        this.stackTrace = stackTrace;
    }

    // Getters và Setters
}
