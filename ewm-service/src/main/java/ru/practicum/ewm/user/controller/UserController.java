package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.PrivateEventService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final PrivateEventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable long userId,
            @Valid @RequestBody NewEventDto newEvent) {
        return eventService.create(userId, newEvent);
    }
}