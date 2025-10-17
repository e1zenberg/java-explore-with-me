package ru.practicum.ewm.web;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.service.EventService;

@RestController
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/events")
public class PublicEventController {

    EventService eventService;

    @GetMapping
    public List<EventShortDto> list(@RequestParam(required = false) String text,
                                    @RequestParam(required = false) List<Long> categories,
                                    @RequestParam(required = false) Boolean paid,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                    LocalDateTime rangeStart,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                    LocalDateTime rangeEnd,
                                    @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                    @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    HttpServletRequest request) {
        return eventService.searchPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request
        );
    }

    @GetMapping("/{id}")
    public EventFullDto get(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getPublicById(id, request);
    }
}
