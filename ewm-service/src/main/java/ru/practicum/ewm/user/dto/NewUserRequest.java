package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank
    @Email
    @Length(min = 6, max = 254, message = "Длина электронной почты должна иметь минимум 6 и максимум 64 символа")
    private String email;

    @NotBlank
    @Length(min = 2, max = 250, message = "Длина имени должна иметь минимум 2 и максимум 250 символов")
    private String name;
}