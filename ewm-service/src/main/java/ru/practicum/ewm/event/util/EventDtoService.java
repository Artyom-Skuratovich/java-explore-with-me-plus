package ru.practicum.ewm.event.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventDtoService {
    private final EventStatsService statsService;

    public List<EventShortDto> buildShortDtoList(
            List<Event> events,
            LocalDateTime start,
            LocalDateTime end,
            String baseUri) {
        final Map<Long, Long> views = statsService.countEventViews(events, start, end, baseUri);
        return events.stream()
                .map(EventMapper::toShortDto)
                .peek(dto -> {
                    dto.setViews(views.getOrDefault(dto.getId(), 0L));
                    dto.setConfirmedRequests(statsService.countConfirmedRequests(dto.getId()));
                })
                .toList();
    }

    public List<EventFullDto> buildFullDtoList(
            List<Event> events,
            LocalDateTime start,
            LocalDateTime end,
            String baseUri) {
        final Map<Long, Long> views = statsService.countEventViews(events, start, end, baseUri);
        return events.stream()
                .map(EventMapper::toFullDto)
                .peek(dto -> {
                    dto.setViews(views.getOrDefault(dto.getId(), 0L));
                    dto.setConfirmedRequests(statsService.countConfirmedRequests(dto.getId()));
                })
                .toList();
    }

    public EventFullDto buildFullDto(
            Event event,
            LocalDateTime start,
            LocalDateTime end,
            String baseUri) {
        final long views = statsService.countEventViews(
                List.of(event), start, end, baseUri).getOrDefault(event.getId(), 0L
        );
        final long confirmedRequests = statsService.countConfirmedRequests(event.getId());
        EventFullDto dto = EventMapper.toFullDto(event);
        dto.setViews(views);
        dto.setConfirmedRequests(confirmedRequests);
        return dto;
    }
}