package ru.practicum.ewm.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.dto.ParticipationRequestStatus;
import ru.practicum.ewm.request.model.RequestStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestStatusMapper {
    public static RequestStatus toRequestStatus(ParticipationRequestStatus status) {
        if (status.equals(ParticipationRequestStatus.REJECTED)) {
            return RequestStatus.REJECTED;
        } else return RequestStatus.CONFIRMED;
    }
}
