package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventSort;
import ru.practicum.ewm.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false, defaultValue = "VIEWS") EventSort sort,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size,
            HttpServletRequest request) {
        return eventService.findAll(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request
        );
    }

    @GetMapping("/{id}")
    public EventFullDto find(@PathVariable long id) {
        return null;
    }
}