package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    // <editor-fold defaultstate="collapsed" desc="Public Api">
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
        final List<EventShortDto> result = new ArrayList<>(mapToShortDtoList(events, rangeStart, rangeEnd));
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
        return mapToFullDto(event);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Private Api">
    @Override
    public List<EventShortDto> findAllUserEvents(long userId, int from, int size) {
        final List<Event> events = eventRepository.findAllByInitiator(userId, PageRequest.of(from, size));
        return mapToShortDtoList(events, LocalDateTime.MIN, LocalDateTime.MAX);
    }

    @Transactional
    @Override
    public EventFullDto createEvent(long userId, NewEventDto eventDto) {
        final User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        final Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id=%d was not found", eventDto.getCategory()))
                );
        final Event saved = eventRepository.save(EventMapper.toEntity(eventDto, category, initiator));
        return mapToFullDto(saved);
    }

    @Override
    public EventFullDto findUserEvent(long userId, long eventId) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        return mapToFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(long userId, long eventId, UpdateEventUserRequest updateRequest) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException(
                    "Only pending or canceled events can be changed",
                    "FORBIDDEN",
                    "For the requested operation the conditions are not met"
            );
        }
        if (updateRequest.getCategory() != null) {
            final Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            String.format("Category with id=%d was not found", updateRequest.getCategory()))
                    );
            event.setCategory(category);
        }
        final Event saved = eventRepository.save(EventMapper.updateProperties(event, updateRequest));
        return mapToFullDto(saved);
    }
    // </editor-fold>

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

    private EventFullDto mapToFullDto(Event event) {
        final EventFullDto fullDto = EventMapper.toFullDto(event);
        fullDto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED));
        final List<ViewStats> viewStats = statsClient.findStats(
                LocalDateTime.MIN, LocalDateTime.MAX, List.of(String.format("/events/%d", event.getId())), true
        );
        fullDto.setViews(viewStats.isEmpty() ? 0L : viewStats.getFirst().getHits());
        return fullDto;
    }

    private List<EventShortDto> mapToShortDtoList(List<Event> events, LocalDateTime start, LocalDateTime end) {
        final Map<Long, Long> eventViews = calculateEventViews(events, start, end);
        return EventMapper.toShortDtoList(events)
                .stream()
                .peek(e ->
                        e.setConfirmedRequests(requestRepository.countByEventIdAndStatus(
                                        e.getId(),
                                        RequestStatus.CONFIRMED
                                )
                        )
                )
                .peek(e -> e.setViews(eventViews.getOrDefault(e.getId(), 0L)))
                .toList();
    }
}