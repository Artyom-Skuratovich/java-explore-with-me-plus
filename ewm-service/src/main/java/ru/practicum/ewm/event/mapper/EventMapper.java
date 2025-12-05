package ru.practicum.ewm.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {
    public static Event from(NewEventDto newEvent, Category category, User initiator) {
        return Event.builder()
                .category(category)
                .initiator(initiator)
                .location(newEvent.getLocation())
                .state(newEvent.isRequestModeration() ? EventState.PENDING : EventState.PUBLISHED)
                .description(newEvent.getDescription())
                .annotation(newEvent.getAnnotation())
                .title(newEvent.getTitle())
                .createdOn(LocalDateTime.now())
                .publishedOn(newEvent.isRequestModeration() ? null : LocalDateTime.now())
                .eventDate(newEvent.getEventDate())
                .paid(newEvent.getPaid())
                .requestModeration(newEvent.isRequestModeration())
                .participantLimit(newEvent.getParticipantLimit())
                .build();
    }

    public static EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(0)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(0)
                .build();
    }
}