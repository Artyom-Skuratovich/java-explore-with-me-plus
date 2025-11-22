package ru.practicum.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.dto.EndpointHit;

public interface HitRepository extends JpaRepository<EndpointHit, Long> {
}
