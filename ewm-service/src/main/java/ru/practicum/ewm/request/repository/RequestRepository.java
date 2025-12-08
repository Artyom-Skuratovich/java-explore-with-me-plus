package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    long countByEventIdAndStatus(long eventId, RequestStatus status);

    @EntityGraph(attributePaths = {"event", "requester"})
    Optional<Request> findByIdAndRequesterId(long id, long requesterId);

    Optional<Request> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    Long countByEventIdAndStatusNot(Long eventId, RequestStatus status);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    @Modifying
    @Query("UPDATE Request r " +
            "SET r.status = :newStatus " +
            "WHERE r.status = :oldStatus AND r.event.id = :eventId")
    void updateStatus(@Param("oldStatus") RequestStatus oldStatus,
                      @Param("newStatus") RequestStatus newStatus,
                      @Param("eventId") Long eventId);

    Optional<List<Request>> findByRequesterId(Long requesterId);

    Optional<Request> findByIdAndRequesterId(Long id, Long requesterId);
}