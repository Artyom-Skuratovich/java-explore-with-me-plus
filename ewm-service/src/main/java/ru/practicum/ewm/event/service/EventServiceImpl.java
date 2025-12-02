package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventSort;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

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
        final HitCreateDto hitCreate = buildHit(request);
        statsClient.hit(hitCreate);
        final List<Event> events = eventRepository.findAllByParameters(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page
        );
        events.forEach(e -> {
            hitCreate.setUri(String.format("/events/%d", e.getId()));
            hitCreate.setTimestamp(LocalDateTime.now());
            statsClient.hit(hitCreate);
        });
        final Map<Long, Long> eventViews = calculateEventViews(events, rangeStart, rangeEnd);
        final List<EventShortDto> result = new ArrayList<>(EventMapper.toShortDtoList(events)
                .stream()
                .peek(e -> e.setConfirmedRequests(requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED)))
                .peek(e -> e.setViews(eventViews.getOrDefault(e.getId(), 0L)))
                .toList());
        if (sort == EventSort.VIEWS) {
            result.sort((e1, e2) -> (int) (e2.getViews() - e1.getViews()));
        }
        return result;
    }


    @Override
    public EventFullDto find(long id, HttpServletRequest request) {
        final Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(
                () -> new NotFoundException(String.format("Event with id=%d was not found", id))
        );
        final HitCreateDto hitCreate = buildHit(request);
        statsClient.hit(hitCreate);
        final EventFullDto result = EventMapper.toFullDto(event);
        result.setConfirmedRequests(requestRepository.countByEventIdAndStatus(id, RequestStatus.CONFIRMED));
        final List<ViewStats> viewStats = statsClient.findStats(
                LocalDateTime.MIN, LocalDateTime.MAX, List.of(String.format("/events/%d", id)), true
        );
        result.setViews(viewStats.isEmpty() ? 0L : viewStats.getFirst().getHits());
        return result;
    }

    private static HitCreateDto buildHit(HttpServletRequest request) {
        return HitCreateDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private Map<Long, Long> calculateEventViews(List<Event> events, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        final Set<String> uris = events.stream()
                .map(e -> String.format("/events/%d", e.getId()))
                .collect(Collectors.toSet());
        final List<ViewStats> viewStats = statsClient.findStats(rangeStart, rangeEnd, uris, true);
        final Map<Long, Long> result = new HashMap<>();
        for (ViewStats vs : viewStats) {
            final long id = Long.parseLong(vs.getUri().split("/events/")[1]);
            result.put(id, result.getOrDefault(id, 0L) + 1);
        }
        return result;
    }
}