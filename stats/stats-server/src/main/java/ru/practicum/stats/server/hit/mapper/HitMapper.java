package ru.practicum.stats.server.hit.mapper;

import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.server.hit.model.Hit;

public final class HitMapper {
    private HitMapper() {
    }

    public static Hit mapToHit(HitCreateDto hitDto) {
        return Hit.builder()
                .uri(hitDto.getUri())
                .app(hitDto.getApp())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }

    public static HitDto mapToDto(Hit hit) {
        return HitDto.builder()
                .id(hit.getId())
                .uri(hit.getUri())
                .app(hit.getApp())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }
}