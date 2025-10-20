package ru.practicum.ewm.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.error.BadRequestException;
import ru.practicum.ewm.service.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
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
                                    @RequestParam(required = false, defaultValue = "false")
                                    Boolean onlyAvailable,
                                    @RequestParam(required = false)
                                    String sort,
                                    @RequestParam(defaultValue = "0") @Min(0)
                                    int from,
                                    @RequestParam(defaultValue = "10") @Positive
                                    int size,
                                    HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Параметр rangeStart не может быть позже rangeEnd");
        }
        return eventService.searchPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request
        );
    }

    @GetMapping("/{id}")
    public EventFullDto get(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getPublicById(id, request);
    }
}
