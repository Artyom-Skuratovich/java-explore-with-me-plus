package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {
    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto cancel(long userId, long requestId);

    List<ParticipationRequestDto> findUserRequests(long userId);

    List<ParticipationRequestDto> findEventRequestsByUser(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request
    );

    ParticipationRequestDto getRequestUserByEventId(Long userId, Long eventId);

    List<ParticipationRequestDto> updateEventRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getRequestByUserId(Long userId);

    ParticipationRequestDto createRequestByEventIdFromUserId(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestByIdAndUserId(Long userId, Long requestId);
}