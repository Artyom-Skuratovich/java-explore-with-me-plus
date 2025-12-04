package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.initiator " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND ((:text) IS NULL OR UPPER(e.annotation) LIKE CONCAT('%', UPPER(:text), '%') " +
            "    OR UPPER(e.description) LIKE CONCAT('%', UPPER(:text), '%')) " +
            "AND ((:categories) IS NULL OR e.category.id IN :categories) " +
            "AND ((:paid) IS NULL OR e.paid = :paid) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND ((:onlyAvailable IS TRUE " +
            "    AND e.participantLimit > (" +
            "        SELECT COUNT(r) FROM Request r " +
            "        WHERE r.event = e AND r.status = 'CONFIRMED')) " +
            "    OR :onlyAvailable IS FALSE)")
    List<Event> findAllByParameters(
            @Param("text") String text,
            @Param("categories") Iterable<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable,
            Pageable page);

    @Query("SELECT e FROM Event e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.initiator " +
            "WHERE e.initiator.id = :initiatorId")
    List<Event> findAllByInitiator(@Param("initiatorId") long initiatorId, Pageable page);

    Optional<Event> findByIdAndInitiatorId(long id, long initiatorId);

    Optional<Event> findByIdAndState(long id, EventState state);
}