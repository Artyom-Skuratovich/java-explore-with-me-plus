package ru.practicum.ewm.compilation.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {

    public static Compilation toEntity(NewCompilationDto dto, List<Event> events) {
        boolean pinned = dto.getPinned() != null ? dto.getPinned() : false;

        return Compilation.builder()
                .title(dto.getTitle())
                .pinned(pinned)
                .events(events)
                .build();
    }

    public static CompilationDto toDto(Compilation compilation, List<EventShortDto> eventDtos) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(eventDtos != null ? eventDtos : Collections.emptyList())
                .build();
    }
}
