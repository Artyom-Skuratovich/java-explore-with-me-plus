package ru.practicum.ewm.event.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class EventHelper {
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    public Map<Long, Long> countEventViews(List<Event> events, LocalDateTime start, LocalDateTime end) {
        final Set<String> uris = events.stream().map(e -> "/events/" + e.getId()).collect(Collectors.toSet());
        final List<ViewStats> stats = statsClient.findStats(start, end, uris, true);

        final HashMap<Long, Long> eventViews = new HashMap<>();
        for (ViewStats s : stats) {
            final long eventId = Long.parseLong(s.getUri().replace("/events/", ""));
            final Long views = eventViews.getOrDefault(eventId, 0L);
            eventViews.put(eventId, views + s.getHits());
        }
        return eventViews;
    }

    public List<EventShortDto> buildShortDtoList(List<Event> events, LocalDateTime start, LocalDateTime end) {
        final Map<Long, Long> views = countEventViews(events, start, end);
        return events.stream()
                .map(EventMapper::toShortDto)
                .peek(e ->
                        e.setConfirmedRequests(
                                requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED)
                        )
                )
                .peek(e -> e.setViews(views.getOrDefault(e.getId(), 0L)))
                .toList();
    }

    public EventFullDto buildFullDto(Event event, LocalDateTime start, LocalDateTime end) {
        final long views = countEventViews(List.of(event), start, end).getOrDefault(event.getId(), 0L);
        final long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        final EventFullDto result = EventMapper.toFullDto(event);
        result.setConfirmedRequests(confirmedRequests);
        result.setViews(views);
        return result;
    }
}