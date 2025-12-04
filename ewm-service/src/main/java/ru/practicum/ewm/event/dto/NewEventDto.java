package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.common.annotation.MinHoursFromNow;
import ru.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;

@Data
@Builder
public class NewEventDto {
    @NotBlank
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    private String description;

    @NotNull
    @MinHoursFromNow(hours = 2)
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    @JsonSetter(nulls = Nulls.SKIP)
    private boolean paid = false;

    @JsonSetter(nulls = Nulls.SKIP)
    private int participantLimit = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    private boolean requestModeration = true;

    @NotBlank
    private String title;
}