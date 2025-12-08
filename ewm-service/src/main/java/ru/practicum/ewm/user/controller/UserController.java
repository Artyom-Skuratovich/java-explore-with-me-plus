package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.PrivateEventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.PrivateRequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final PrivateEventService eventService;
    private final PrivateRequestService requestService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findUserEvents(
            @PathVariable long userId,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "0") int size) {
        return eventService.findUserEvents(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable long userId,
            @Valid @RequestBody NewEventDto newEvent) {
        return eventService.create(userId, newEvent);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findEvent(
            @PathVariable long userId,
            @PathVariable long eventId) {
        return eventService.findUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @PathVariable long userId,
            @PathVariable long eventId,
            @Valid @RequestBody UpdateEventUserRequest updatedEvent) {
        return eventService.update(userId, eventId, updatedEvent);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<ParticipationRequestDto> getRequestUserByEventId(@PathVariable(name = "userId") Long userId,
                                                                           @PathVariable(name = "eventId") Long eventId) {
        return ResponseEntity.ok(requestService.getRequestUserByEventId(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> updateRequestStatus(@Valid @RequestBody EventRequestStatusUpdateRequest updateRequest,
                                                                             @PathVariable(name = "userId") Long userId,
                                                                             @PathVariable(name = "eventId") Long eventId) {
        return ResponseEntity.ok(requestService.updateEventRequestStatus(userId, eventId, updateRequest));
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestByUserId(@PathVariable(name = "userId") Long userId) {
        return ResponseEntity.ok(requestService.getRequestByUserId(userId));
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequestToEventId(@PathVariable(name = "userId") Long userId,
                                                                          @RequestParam(name = "eventId") Long eventId) {
        return ResponseEntity.ok(requestService.createRequestByEventIdFromUserId(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequestByIdAndUserId(@PathVariable(name = "userId") Long userId,
                                                                              @PathVariable(name = "requestId") Long requestId) {
        return ResponseEntity.ok(requestService.cancelRequestByIdAndUserId(userId, requestId));
    }
}