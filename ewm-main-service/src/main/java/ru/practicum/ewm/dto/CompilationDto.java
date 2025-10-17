package ru.practicum.ewm.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;
    Boolean pinned;
    String title;
    List<EventShortDto> events;
}
