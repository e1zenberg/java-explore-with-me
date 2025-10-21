package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    Long id;
    String annotation;
    CategoryDto category;
    String description;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime eventDate;

    Location location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String title;
    UserShortDto initiator;
    EventState state;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime createdOn;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    LocalDateTime publishedOn;

    Long views;
    Long confirmedRequests;
}
