package ru.practicum.stats.server.statistics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.statistics.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public List<ViewStats> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return unique ?
                statsRepository.findStatsUnique(start, end, uris) :
                statsRepository.findStatsAll(start, end, uris);
    }
}