package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    long countByEventIdAndStatus(long eventId, RequestStatus status);

    @EntityGraph(attributePaths = {"event", "requester"})
    Optional<Request> findByIdAndRequesterId(long id, long requesterId);
}