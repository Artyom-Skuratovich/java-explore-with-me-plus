package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;

public interface PublicCompilationService {
    CompilationDto find(long id);
}