package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventSort;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public List<EventShortDto> findAll(String text,
                                       Set<Long> categories,
                                       Boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       boolean onlyAvailable,
                                       EventSort sort,
                                       int from,
                                       int size,
                                       HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("The rangeStart must be earlier than the rangeEnd");
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
        }
        final Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        final List<Event> events = eventRepository.findAllByParameters(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page
        );
    }

    @Override
    public EventFullDto find(long id) {
        return null;
    }
}