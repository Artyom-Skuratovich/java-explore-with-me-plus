package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto getRequestUserByEventId(Long userId, Long eventId);

    List<ParticipationRequestDto> updateEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getRequestByUserId(Long userId);

    ParticipationRequestDto createRequestByEventIdFromUserId(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestByIdAndUserId(Long userId, Long requestId);
}