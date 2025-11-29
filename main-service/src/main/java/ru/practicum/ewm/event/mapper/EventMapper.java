package ru.practicum.ewm.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventShortDto toShortDto(Event event) {
        EventShortDto dto = new EventShortDto();
        //когда будет структура EventShortDto — заполнить поля
        return dto;
    }

    public static List<EventShortDto> toShortDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toShortDto)
                .toList();
    }
}
