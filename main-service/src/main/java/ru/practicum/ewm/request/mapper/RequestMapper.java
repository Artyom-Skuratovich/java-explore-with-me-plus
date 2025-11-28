package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

public final class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus());
    }

    public static Request toRequest(ParticipationRequestDto participationRequestDto, User requestor, Event event) {
        Request request = new Request();
        request.setRequester(requestor);
        request.setStatus(RequestStatus.PENDING);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}
