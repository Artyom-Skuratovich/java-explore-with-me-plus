package ru.practicum.stats.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HitDto {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}