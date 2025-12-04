package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.dto.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {
    List<EventShortDto> findAll(String text,
                                Set<Long> categories,
                                Boolean paid,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                boolean onlyAvailable,
                                EventSort sort,
                                int from,
                                int size,
                                HttpServletRequest request);

    EventFullDto find(long id, HttpServletRequest request);

    List<EventShortDto> findAllUserEvents(long userId, int from, int size);

    EventFullDto createEvent(long userId, NewEventDto eventDto);

    EventFullDto findUserEvent(long userId, long eventId);

    EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest updateRequest);
}