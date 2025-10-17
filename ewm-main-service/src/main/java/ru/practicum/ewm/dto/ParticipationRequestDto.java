package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.RequestStatus;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    java.time.LocalDateTime created;
    Long event;
    Long requester;
    RequestStatus status;
}
