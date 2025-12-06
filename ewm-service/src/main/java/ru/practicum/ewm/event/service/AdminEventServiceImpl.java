package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.exception.BadRequestException;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.dto.AdminEventAction;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.HitMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.util.EventHelper;
import ru.practicum.ewm.event.util.EventDateTimeUtils;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;
    private final EventHelper helper;

    @Override
    public List<EventFullDto> findAllByCriteria(
            Iterable<Long> users,
            Iterable<EventState> states,
            Iterable<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size,
            HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = EventDateTimeUtils.defaultStart();
        }
        if (rangeEnd == null) {
            rangeEnd = EventDateTimeUtils.defaultEnd();
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("The rangeStart must be earlier than or equal to the rangeEnd");
        }
        HitDto ignored = statsClient.hit(HitMapper.buildCreateHit(request));
        final List<Event> events = eventRepository.findAllByCriteria(
                users, states, categories, rangeStart, rangeEnd, PageRequest.of(from, size)
        ).stream().toList();
        helper.sendHits(events, request);
        return helper.buildFullDtoList(events, rangeStart, rangeEnd);
    }

    @Override
    @Transactional
    public EventFullDto update(long id, UpdateEventAdminRequest updatedEvent, HttpServletRequest request) {
        HitDto ignored = statsClient.hit(HitMapper.buildCreateHit(request));
        final Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
        final Long categoryId = updatedEvent.getCategory();

        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
        }

        final AdminEventAction stateAction = updatedEvent.getStateAction();
        if (stateAction == AdminEventAction.PUBLISH_EVENT && event.getState() == EventState.CANCELED) {
            throw new ConflictException("Cannot publish the event because it's not in the right state: PENDING");
        } else if (stateAction == AdminEventAction.REJECT_EVENT && event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot reject the event because it's already been published");
        }

        EventMapper.updateEventProperties(updatedEvent, event, category);
        ensureStartDateIsAtLeastAnHourAfterPublication(event.getEventDate(), event.getPublishedOn());
        return helper.buildFullDto(
                eventRepository.save(event), EventDateTimeUtils.defaultStart(), EventDateTimeUtils.defaultEnd()
        );
    }

    public static void ensureStartDateIsAtLeastAnHourAfterPublication(
            LocalDateTime startDate,
            LocalDateTime publicationDate) {
        if (publicationDate != null && startDate.isBefore(publicationDate.plusHours(1))) {
            throw new ConflictException("Start date must be at least one hour after publication date");
        }
    }
}