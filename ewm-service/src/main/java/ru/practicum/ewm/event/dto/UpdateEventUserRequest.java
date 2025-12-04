package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.common.annotation.MinHoursFromNow;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.stats.common.Constants;

import java.time.LocalDateTime;

@Data
@Builder
public class UpdateEventUserRequest {
    private String annotation;
    private Long category;
    private String description;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @MinHoursFromNow(hours = 2)
    private LocalDateTime eventDate;

    private Location location;
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private StateAction stateAction;
    private Boolean requestModeration;
    private String title;
}