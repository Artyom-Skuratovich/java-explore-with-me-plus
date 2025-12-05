package ru.practicum.ewm.event.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventUtils {
    public static Map<Long, Long> countEventViews(
            List<Event> events,
            LocalDateTime start,
            LocalDateTime end,
            StatsClient statsClient) {
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
}