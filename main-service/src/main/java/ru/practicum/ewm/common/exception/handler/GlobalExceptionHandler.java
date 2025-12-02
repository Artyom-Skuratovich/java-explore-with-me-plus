package ru.practicum.ewm.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.common.exception.BadRequestException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        final ErrorResponse response = ErrorResponse.builder()
                .status("BAD_REQUEST")
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp("")
                .build();
        return ResponseEntity.status(400).body(response);
    }
}