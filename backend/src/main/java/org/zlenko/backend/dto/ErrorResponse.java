package org.zlenko.backend.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse(
        int status,
        String message,
        String path,
        Instant timestamp
) {
    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return new ErrorResponse(status.value(), message, path, Instant.now());
    }
}
