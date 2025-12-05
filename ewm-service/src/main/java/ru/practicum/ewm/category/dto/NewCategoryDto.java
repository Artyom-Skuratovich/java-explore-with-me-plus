package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "The category name must not be empty")
    private String name;
}