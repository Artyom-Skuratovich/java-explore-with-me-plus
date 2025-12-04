package ru.practicum.ewm.common.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.stats.common.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException e) {
        final ApiError error = ApiError.builder()
                .status("BAD_REQUEST")
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp(getTimestampAsString())
                .build();
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        final ApiError error = ApiError.builder()
                .status("NOT_FOUND")
                .reason("The required object was not found")
                .message(e.getMessage())
                .timestamp(getTimestampAsString())
                .build();
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException e) {
        final ApiError error = ApiError.builder()
                .status(e.getStatus())
                .reason("Integrity constraint has been violated")
                .message(e.getMessage())
                .timestamp(getTimestampAsString())
                .build();
        return ResponseEntity.status(409).body(error);
    }

    private static String getTimestampAsString() {
        return LocalDateTime.now().format(FORMATTER);
    }
}