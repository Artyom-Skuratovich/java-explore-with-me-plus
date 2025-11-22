package ru.practicum.stats.server.hit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.server.hit.service.HitService;

@RestController
@RequestMapping("/hit")
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping
    public HitDto create(@Valid @RequestBody HitCreateDto hitDto) {
        return hitService.create(hitDto);
    }
}
