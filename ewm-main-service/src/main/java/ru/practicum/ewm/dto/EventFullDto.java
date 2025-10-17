package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.EventState;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    java.time.LocalDateTime eventDate;
    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String title;
    UserShortDto initiator;
    EventState state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    java.time.LocalDateTime createdOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    java.time.LocalDateTime publishedOn;
    Long views;
    Long confirmedRequests;
}
