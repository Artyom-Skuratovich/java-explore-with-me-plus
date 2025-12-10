package ru.practicum.ewm.event.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
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

        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        final Set<String> uris = events.stream()
                .map(e -> baseUri + "/" + e.getId())
                .collect(Collectors.toSet());

        List<ViewStats> stats;
        try {
            stats = statsClient.findStats(start, end, uris, true);
        } catch (Exception ex) {
            log.warn("Failed to get stats from stats-service: {}", ex.getMessage());
            return Collections.emptyMap();
        }

        final HashMap<Long, Long> eventViews = new HashMap<>();
        for (ViewStats s : stats) {
            final String strId = s.getUri().replace(baseUri + "/", "");
            if (!strId.equals(baseUri)) {
                final long eventId = Long.parseLong(strId);
                final long views = eventViews.getOrDefault(eventId, 0L);
                eventViews.put(eventId, views + s.getHits());
            }
        }
        return eventViews;
    }

    public long countConfirmedRequests(long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    public void sendHits(List<Event> events, HttpServletRequest request) {
        if (events.isEmpty()) {
            return;
        }

        final HitCreateDto hit = HitMapper.buildCreateHit(request);
        events.forEach(e -> {
            hit.setUri(request.getRequestURI() + "/" + e.getId());
            hit.setTimestamp(LocalDateTime.now());
            try {
                statsClient.hit(hit);
            } catch (Exception ex) {
                log.warn("Failed to send hit for event {} to stats-service: {}", e.getId(), ex.getMessage());
            }
        });
    }

    public void sendHit(HttpServletRequest request) {
        final HitCreateDto hit = HitMapper.buildCreateHit(request);
        try {
            HitDto ignored = statsClient.hit(hit);
        } catch (Exception ex) {
            log.warn("Failed to send hit to stats-service: {}", ex.getMessage());
        }
    }
}
