package ru.practicum.ewm.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
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