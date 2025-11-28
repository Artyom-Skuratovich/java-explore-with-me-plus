package ru.practicum.ewm.request.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    @NotNull(message = "ID запросов для обновления статуса должно быть заполнено.")
    private List<Long> requestIds;

    @NotNull(message = "Статус подтверждения должен быть указан.")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
