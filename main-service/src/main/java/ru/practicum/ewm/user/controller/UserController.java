package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RequestService requestService;

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