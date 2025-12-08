package ru.practicum.ewm.request.service;

import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(long userId, long eventId) {
        return null;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancel(long userId, long requestId) {
        final Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        request.setStatus(RequestStatus.REJECTED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> findUserRequests(long userId) {
        return List.of();
    }

    @Override
    public List<ParticipationRequestDto> findEventRequestsByUser(long userId, long eventId) {
        return List.of();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest request) {
        return null;
    }
}