package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

public interface RequestRepository extends JpaRepository<Request, Long> {
    long countByEventIdAndStatus(long eventId, RequestStatus status);
}