package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.PublicCompilationService;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final PublicCompilationService compilationService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto find(@PathVariable long id) {
        return compilationService.find(id);
    }
}