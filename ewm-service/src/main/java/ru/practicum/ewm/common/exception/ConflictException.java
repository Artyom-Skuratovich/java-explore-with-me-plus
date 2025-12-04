package ru.practicum.ewm.common.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    private final String status;
    private final String reason;

    public ConflictException(String message, String status, String reason) {
        super(message);
        this.status = status;
        this.reason = reason;
    }
}