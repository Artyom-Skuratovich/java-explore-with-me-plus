package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<ParticipationRequestDto> getRequestUserByEventId(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId) {
        return ResponseEntity.ok(requestService.getRequestUserByEventId(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> updateRequestStatus(
            @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest,
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId) {
        return ResponseEntity.ok(requestService.updateEventRequestStatus(userId, eventId, updateRequest));
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestByUserId(
            @PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(requestService.getRequestByUserId(userId));
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequestToEventId(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "eventId") Long eventId) {
        return ResponseEntity.ok(requestService.createRequestByEventIdFromUserId(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequestByIdAndUserId(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "requestId") Long requestId) {
        return ResponseEntity.ok(requestService.cancelRequestByIdAndUserId(userId, requestId));
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findAllUserEvents(
            @PathVariable long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return eventService.findAllUserEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(
            @PathVariable long userId,
            @Valid @RequestBody NewEventDto event) {
        return eventService.createEvent(userId, event);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findUserEvent(
            @PathVariable long userId,
            @PathVariable long eventId) {
        return eventService.findUserEvent(userId, eventId);
    }
}