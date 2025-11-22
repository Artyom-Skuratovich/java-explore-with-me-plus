package ru.practicum.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHit {
    @Id
    private Long id;

    @NotBlank(message = "Приложение, из которого происходит вызов, должно быть указано.")
    private String app;

    @NotBlank(message = "Строка вызова должна быть указана.")
    private String uri;

    @NotBlank(message = "IP Адрес, откуда происходит вызов, должен быть указан.")
    private String ip;

    @NotNull(message = "Время вызова должно быть указано.")
    private LocalDateTime timestamp;
}