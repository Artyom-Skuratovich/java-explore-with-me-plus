package ru.practicum.stats.service.statistics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.common.Constants;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.service.statistics.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> findStats(
            @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) @RequestParam LocalDateTime start,
            @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT) @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return statsService.findStats(start, end, uris, unique);
    }
}