package ru.practicum.ewm.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

@RestController
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users/{userId}")
public class PrivateEventController {

    EventService eventService;
    RequestService requestService;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
        return eventService.create(userId, dto);
    }

    @GetMapping("/events")
    public List<EventShortDto> myEvents(@PathVariable Long userId,
                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto myEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest dto) {
        return eventService.updateByUser(userId, eventId, dto);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> eventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateStatuses(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @Valid @RequestBody EventRequestStatusUpdateRequest body) {
        return requestService.updateStatuses(userId, eventId, body);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> myRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto request(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancel(userId, requestId);
    }
}
