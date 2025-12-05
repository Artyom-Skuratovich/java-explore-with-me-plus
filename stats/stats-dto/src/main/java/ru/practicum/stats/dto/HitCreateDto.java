package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.stats.common.Constants;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HitCreateDto {
    @NotBlank(message = "Приложение, из которого происходит вызов, должно быть указано.")
    private String app;

    @NotBlank(message = "Строка вызова должна быть указана.")
    private String uri;

    @NotBlank(message = "IP Адрес, откуда происходит вызов, должен быть указан.")
    private String ip;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT)
    @NotNull(message = "Время вызова должно быть указано.")
    private LocalDateTime timestamp;
}