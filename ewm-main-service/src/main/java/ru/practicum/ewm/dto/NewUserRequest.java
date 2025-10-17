package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    @NotBlank(message = "Имя не может быть пустым.")
    @Size(max = 255, message = "Имя слишком длинное.")
    String name;

    @NotBlank(message = "Email не может быть пустым.")
    @Email(message = "Email имеет неверный формат.")
    @Size(max = 512, message = "Email слишком длинный.")
    String email;
}
