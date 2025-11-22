package ru.practicum.stats.server.hit.service;

import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;

public interface HitService {
    HitDto create(HitCreateDto hitDto);
}
