package ru.practicum.ewm.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .paid(event.isPaid())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .category(CategoryMapper.toDto(event.getCategory()))
                .build();
    }

    public static List<EventShortDto> toShortDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toShortDto)
                .toList();
    }

    public static EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .paid(event.isPaid())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .category(CategoryMapper.toDto(event.getCategory()))
                .location(event.getLocation())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .description(event.getDescription())
                .build();
    }

    public static Event toEntity(NewEventDto newEventDto, Category category, User initiator) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(newEventDto.getLocation())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .createdOn(LocalDateTime.now())
                .category(category)
                .initiator(initiator)
                .state(EventState.PENDING)
                .build();
    }

    public static Event updateProperties(Event event, UpdateEventUserRequest request) {
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        StateAction stateAction = request.getStateAction();
        if (stateAction == StateAction.CANCEL_REVIEW) {
            event.setState(EventState.CANCELED);
        } else if (stateAction == StateAction.SEND_TO_REVIEW) {
            event.setState(EventState.PENDING);
        }
        return event;
    }
}