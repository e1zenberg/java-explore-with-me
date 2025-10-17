package ru.practicum.ewm.web;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.service.EventService;

@RestController
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/admin/events")
public class AdminEventController {

    EventService eventService;

    @GetMapping
    public List<EventFullDto> search(@RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<EventState> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime rangeStart,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                     LocalDateTime rangeEnd,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return eventService.searchAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequest dto) {
        return eventService.updateByAdmin(eventId, dto);
    }
}
