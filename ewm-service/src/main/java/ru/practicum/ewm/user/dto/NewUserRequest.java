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
    @Length(min = 6, max = 254, message = "The email address must be between 6 and 64 characters long")
    private String email;

    @NotBlank
    @Length(min = 2, max = 250, message = "The name must be between 2 and 250 characters long")
    private String name;
}