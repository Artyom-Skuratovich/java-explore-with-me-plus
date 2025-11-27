package ru.practicum.ewm.event.model;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Embeddable
public class Location {
    private double lat;
    private double lon;
}