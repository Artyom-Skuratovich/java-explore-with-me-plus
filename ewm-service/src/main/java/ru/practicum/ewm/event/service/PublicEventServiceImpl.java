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
import ru.practicum.ewm.event.dto.EventSortOption;
import ru.practicum.ewm.event.mapper.HitMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.util.EventHelper;
import ru.practicum.ewm.event.util.EventDateTimeUtils;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final EventHelper helper;

    @Override
    public List<EventShortDto> findEventsByCriteria(
            String text,
            Iterable<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSortOption sort,
            int from,
            int size,
            HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().plusNanos(1);
        }
        if (rangeEnd == null) {
            rangeEnd = EventDateTimeUtils.defaultEnd();
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("The rangeStart must be earlier than or equal to the rangeEnd");
        }
        HitDto ignored = statsClient.hit(HitMapper.buildCreateHit(request));

        final Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));
        final List<Event> events = eventRepository.findAllPublishedByCriteria(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable
        ).stream().toList();
        final List<EventShortDto> result = new ArrayList<>(helper.buildShortDtoList(events, rangeStart, rangeEnd));
        helper.sendHits(events, request);
        if (sort == EventSortOption.VIEWS) {
            result.sort((e1, e2) -> (int) (e2.getViews() - e1.getViews()));
        }
        return result;
    }

    @Override
    public EventFullDto findPublishedEvent(long id, HttpServletRequest request) {
        HitDto ignored = statsClient.hit(HitMapper.buildCreateHit(request));
        final Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " not found or not published"));
        return helper.buildFullDto(event, EventDateTimeUtils.defaultStart(), EventDateTimeUtils.defaultEnd());
    }
}