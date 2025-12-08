package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.mapper.RequestStatusMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateRequestServiceImpl implements PrivateRequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

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

    @Override
    public List<ParticipationRequestDto> getRequestUserByEventId(Long userId, Long eventId) {
        List<Request> requestInDb = requestRepository.findByEventId(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Не найден запрос на участие с параметрами " +
                        "userId: %d, eventId: %d", userId, eventId)));
        return requestInDb.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public List<ParticipationRequestDto> updateEventRequestStatus(Long userId,
                                                                  Long eventId,
                                                                  EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с данным id: %d не найдено", eventId)));
        Long participantActual = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0) {
            if (participantActual >= event.getParticipantLimit()) {
                throw new ConflictException("Превышено количество одобренных заявок");
            }
        }
        List<ParticipationRequestDto> responseList = new ArrayList<>();
        for (Long id : updateRequest.getRequestIds()) {
            Request request = requestRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %d не найден", id)));
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Невозможно изменить статус");
            }
            request.setStatus(RequestStatusMapper.toRequestStatus(updateRequest.getStatus()));
            requestRepository.save(request);
            participantActual++;
            if (participantActual == event.getParticipantLimit()) {
                requestRepository.updateStatus(RequestStatus.PENDING, RequestStatus.REJECTED, eventId);
                break;
            }
            responseList.add(RequestMapper.toParticipationRequestDto(request));
        }
        return responseList;
    }

    @Override
    public List<ParticipationRequestDto> getRequestByUserId(Long userId) {
        List<Request> requestList = requestRepository.findByRequesterId(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", userId)));
        return requestList.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto createRequestByEventIdFromUserId(Long userId, Long eventId) {
        Request participationRequestDto = requestRepository.findByEventIdAndRequesterId(eventId, userId)
                .orElse(null);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id: %d не найдено", eventId)));
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id: %d не найден", userId)));
        if (participationRequestDto != null) {
            throw new ConflictException(String.format("Запрос от пользователя под userId: %d для события c eventId: %d уже существует", userId, eventId));
        }
        if (event.getInitiator().getId() == requestor.getId()) {
            throw new ConflictException("Пользователь не может отправлять заявки на участие на свое же событие");
        }
        if (event.getState().equals(EventState.CANCELED) || event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Пользователь не может отправлять заявки на участие на неопубликованное событие");
        }
        Long countEventParticipant = requestRepository.countByEventIdAndStatusNot(eventId, RequestStatus.REJECTED);
        if (!event.isRequestModeration())
            if (event.getParticipantLimit() == countEventParticipant) {
                throw new ConflictException("Превышает количество запросов на участие в данном событии");
            }
        Request request = new Request();
        request.setEvent(event);
        request.setRequester(requestor);
        request.setCreated(LocalDateTime.now());
        if (event.isRequestModeration()) {
            if (event.getParticipantLimit() == 0) {
                request.setStatus(RequestStatus.CONFIRMED);
            } else {
                request.setStatus(RequestStatus.PENDING);
            }
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequestByIdAndUserId(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Ваш запрос на участие с данным id: %d не найден", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }
}