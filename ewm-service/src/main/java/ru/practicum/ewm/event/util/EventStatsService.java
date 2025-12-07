package ru.practicum.ewm.event.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.mapper.HitMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitCreateDto;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventStatsService {
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    public Map<Long, Long> countEventViews(
            List<Event> events,
            LocalDateTime start,
            LocalDateTime end,
            String baseUri) {
        final Set<String> uris = events.stream()
                .map(e -> baseUri + "/" + e.getId())
                .collect(Collectors.toSet());
        final List<ViewStats> stats = statsClient.findStats(start, end, uris, false);

        final HashMap<Long, Long> eventViews = new HashMap<>();
        for (ViewStats s : stats) {
            final long eventId = Long.parseLong(s.getUri().replace(baseUri + "/", ""));
            final long views = eventViews.getOrDefault(eventId, 0L);
            eventViews.put(eventId, views + s.getHits());
        }
        return eventViews;
    }

    public long countConfirmedRequests(long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    public void sendHits(List<Event> events, HttpServletRequest request) {
        final HitCreateDto hit = HitMapper.buildCreateHit(request);
        events.forEach(e -> {
            hit.setUri(request.getRequestURI() + "/" + e.getId());
            hit.setTimestamp(LocalDateTime.now());
            statsClient.hit(hit);
        });
    }

    public void sendHit(HttpServletRequest request) {
        final HitCreateDto hit = HitMapper.buildCreateHit(request);
        final HitDto ignored = statsClient.hit(hit);
    }
}