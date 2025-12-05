package ru.practicum.stats.service.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.stats.common.StatsApiError;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.stats.common.Constants.DATE_TIME_FORMAT;

@ControllerAdvice
public final class GlobalExceptionHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StatsApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final StatsApiError error = StatsApiError.builder()
                .status("BAD_REQUEST")
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StatsApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        final StatsApiError error = StatsApiError.builder()
                .status("BAD_REQUEST")
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<StatsApiError> handleThrowable(Throwable e) {
        final StatsApiError error = StatsApiError.builder()
                .status("INTERNAL_SERVER_ERROR")
                .reason("On the server, an unexpected error occurred")
                .message(e.getMessage())
                .timestamp(getCurrentTimestampAsString())
                .build();
        return ResponseEntity.status(500).body(error);
    }

    private static String getCurrentTimestampAsString() {
        return LocalDateTime.now().format(FORMATTER);
    }
}