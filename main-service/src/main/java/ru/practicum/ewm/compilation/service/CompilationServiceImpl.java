package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        List<Event> events = dto.getEvents() == null || dto.getEvents().isEmpty()
                ? Collections.emptyList()
                : eventRepository.findAllById(dto.getEvents());

        Compilation compilation = CompilationMapper.toEntity(dto, events);
        compilation = compilationRepository.save(compilation);

        return CompilationMapper.toDto(
                compilation,
                EventMapper.toShortDtoList(compilation.getEvents())
        );
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Compilation with id=" + compId + " was not found"
            );
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Compilation with id=" + compId + " was not found"
                ));

        if (request.getTitle() != null) {
            compilation.setTitle(request.getTitle());
        }
        if (request.getPinned() != null) {
            compilation.setPinned(request.getPinned());
        }
        if (request.getEvents() != null) {
            List<Event> events = request.getEvents().isEmpty()
                    ? Collections.emptyList()
                    : eventRepository.findAllById(request.getEvents());
            compilation.setEvents(events);
        }

        compilation = compilationRepository.save(compilation);

        return CompilationMapper.toDto(
                compilation,
                EventMapper.toShortDtoList(compilation.getEvents())
        );
    }
}
