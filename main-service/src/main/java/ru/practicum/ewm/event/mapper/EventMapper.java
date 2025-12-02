package ru.practicum.ewm.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .paid(event.isPaid())
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .initiator(null)
                .category(CategoryMapper.toDto(event.getCategory()))
                .build();
    }

    public static List<EventShortDto> toShortDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toShortDto)
                .toList();
    }
}