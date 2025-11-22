package ru.practicum.stats.server.hit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.server.hit.mapper.HitMapper;
import ru.practicum.stats.server.hit.model.Hit;
import ru.practicum.stats.server.hit.repository.HitRepository;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    @Override
    public HitDto create(HitCreateDto hitDto) {
        final Hit saved = hitRepository.save(HitMapper.mapToHit(hitDto));
        return HitMapper.mapToDto(saved);
    }
}
