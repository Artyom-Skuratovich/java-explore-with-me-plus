package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventSort;

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

    EventFullDto find(long id);
}