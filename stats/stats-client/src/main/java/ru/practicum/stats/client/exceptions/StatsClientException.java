package ru.practicum.stats.client.exceptions;

import lombok.Getter;

@Getter
public class StatsClientException extends RuntimeException {

    private final int status;
    private final String responseBody;

    public StatsClientException(int status, String responseBody, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.responseBody = responseBody;
    }

}