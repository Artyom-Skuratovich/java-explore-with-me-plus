package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.util.EventUtils;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto create(long userId, NewEventDto newEvent) {
        final User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        final long categoryId = newEvent.getCategory();
        final Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
        final Event saved = eventRepository.save(EventMapper.from(newEvent, category, initiator));
        return EventMapper.toFullDto(saved);
    }

    @Override
    public List<EventShortDto> findUserEvents(long userId, int from, int size) {
        return List.of();
    }

    @Override
    public EventFullDto findUserEvent(long userId, long eventId) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        final EventFullDto result = EventMapper.toFullDto(event);
        final long views = EventUtils.countEventViews(List.of(event), LocalDateTime.MIN, LocalDateTime.MAX, statsClient)
                .getOrDefault(eventId, 0L);
        result.setViews(views);
        // TODO: Подсчёт confirmedRequests.
        return result;
    }
}