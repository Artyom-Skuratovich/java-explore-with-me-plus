package ru.practicum.ewm.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

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
}