package ru.practicum.hit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.EndpointHit;
import ru.practicum.hit.service.HitService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/hit")
@RequiredArgsConstructor
public class HitController {

    private final HitService hitService;

    @PostMapping
    public ResponseEntity<Void> saveHit(@RequestBody EndpointHit endpointHit) {
        endpointHit.setTimestamp(LocalDateTime.now());
        hitService.save(endpointHit);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
