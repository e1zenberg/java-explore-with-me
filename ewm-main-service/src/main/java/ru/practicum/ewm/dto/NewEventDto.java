package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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
public class NewEventDto {

    @NotBlank(message = "Аннотация не может быть пустой.")
    @Size(min = 20, max = 2000, message = "Аннотация должна быть от 20 до 2000 символов.")
    String annotation;

    @NotNull(message = "Категория обязательна.")
    Long category;

    @NotBlank(message = "Описание не может быть пустым.")
    @Size(min = 20, max = 7000, message = "Описание должно быть от 20 до 7000 символов.")
    String description;

    @NotNull(message = "Дата события обязательна.")
    @Future(message = "Дата события должна быть в будущем.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    @NotNull(message = "Локация обязательна.")
    Location location;

    @Builder.Default
    Boolean paid = false;

    @PositiveOrZero(message = "Лимит участников не может быть отрицательным.")
    @Builder.Default
    Integer participantLimit = 0;

    @Builder.Default
    Boolean requestModeration = true;

    @NotBlank(message = "Заголовок не может быть пустым.")
    @Size(min = 3, max = 120, message = "Заголовок должен быть от 3 до 120 символов.")
    String title;
}
