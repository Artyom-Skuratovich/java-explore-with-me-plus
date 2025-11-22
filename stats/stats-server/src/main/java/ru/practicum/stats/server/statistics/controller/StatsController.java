package ru.practicum.stats.server.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.common.Constants;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.server.statistics.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping
    public List<ViewStats> findStats(
            @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) @RequestParam LocalDateTime start,
            @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return statsService.findStats(start, end, uris, unique);
    }
}