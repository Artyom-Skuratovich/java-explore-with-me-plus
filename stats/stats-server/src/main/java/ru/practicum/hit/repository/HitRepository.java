package ru.practicum.hit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.EndpointHit;

@Repository
public interface HitRepository extends JpaRepository<EndpointHit, Long> {
}
