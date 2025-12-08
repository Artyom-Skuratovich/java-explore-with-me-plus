package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewm.event.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.stats.common.Constants.DATE_TIME_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank
    @Length(min = 20, max = 2000, message = "Примечание должно иметь не меньше 20 и не больше 2000 символов.")
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Length(min = 20, max = 7000, message = "Описание должно иметь не меньше 20 и не больше 7000 символов.")
    private String description;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @NotNull
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid = false;

    @PositiveOrZero(message = "Число участников не может быть отрицательным значением.")
    private int participantLimit = 0;
    private boolean requestModeration = true;

    @NotBlank
    @Length(min = 3, max = 120, message = "Название события должно иметь минимум 3 и максимум 120 символов.")
    private String title;
}