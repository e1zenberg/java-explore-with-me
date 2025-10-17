package ru.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.error.BadRequestException;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.ForbiddenException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repo.EventRepository;
import ru.practicum.ewm.repo.ParticipationRequestRepository;
import ru.practicum.ewm.stats.StatsFacade;
import ru.practicum.ewm.util.PageUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EventService {

    EventRepository eventRepository;
    ParticipationRequestRepository requestRepository;
    UserService userService;
    CategoryService categoryService;
    StatsFacade stats;

    @Transactional
    public EventFullDto create(Long userId, NewEventDto dto) {
        validateEventDateAtLeast2Hours(dto.getEventDate(), true);
        User initiator = userService.getOr404(userId);
        Category category = categoryService.getOr404(dto.getCategory());
        Event entity = EventMapper.toEntity(dto, initiator, category);
        entity.setCreatedOn(LocalDateTime.now());
        entity.setState(EventState.PENDING);
        Event saved = eventRepository.save(entity);
        return EventMapper.toFullDto(saved, 0, 0);
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        Pageable p = PageUtils.offsetPage(from, size, Sort.by("id").ascending());
        List<Event> list = eventRepository.findAllByInitiator_Id(userId, p);
        return withShortViews(list);
    }

    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event e = getOr404(eventId);
        if (!Objects.equals(e.getInitiator().getId(), userId)) {
            throw new NotFoundException("Событие не найдено для пользователя: " + eventId);
        }
        return withFullViews(e);
    }

    @Transactional
    public EventFullDto updateByUser(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event e = getOr404(eventId);
        if (!Objects.equals(e.getInitiator().getId(), userId)) {
            throw new NotFoundException("Событие не найдено для пользователя: " + eventId);
        }
        if (e.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Опубликованное событие нельзя изменять");
        }
        if (dto.getEventDate() != null) {
            validateEventDateAtLeast2Hours(dto.getEventDate(), false);
        }
        Category cat = dto.getCategory() == null ? null : categoryService.getOr404(dto.getCategory());
        EventMapper.applyUserUpdate(e, dto, cat);
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "SEND_TO_REVIEW" -> e.setState(EventState.PENDING);
                case "CANCEL_REVIEW" -> e.setState(EventState.CANCELED);
                default -> throw new ForbiddenException("Недопустимое действие: " + dto.getStateAction());
            }
        }
        Event saved = eventRepository.save(e);
        return withFullViews(saved);
    }

    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event e = getOr404(eventId);
        if (dto.getEventDate() != null) {
            validateEventDateAtLeast2Hours(dto.getEventDate(), false);
        }
        Category cat = dto.getCategory() == null ? null : categoryService.getOr404(dto.getCategory());
        EventMapper.applyAdminUpdate(e, dto, cat);
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case "PUBLISH_EVENT" -> {
                    if (e.getState() != EventState.PENDING) {
                        throw new ConflictException("Опубликовать можно только событие в статусе PENDING");
                    }
                    e.setState(EventState.PUBLISHED);
                    e.setPublishedOn(LocalDateTime.now());
                }
                case "REJECT_EVENT" -> {
                    if (e.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Нельзя отклонить уже опубликованное событие");
                    }
                    e.setState(EventState.CANCELED);
                }
                default -> throw new ForbiddenException("Недопустимое действие: " + dto.getStateAction());
            }
        }
        Event saved = eventRepository.save(e);
        return withFullViews(saved);
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> searchPublic(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            String sort,
                                            int from,
                                            int size,
                                            HttpServletRequest request) {
        try {
            stats.hit(request);
        } catch (Exception ex) {
            log.debug("Stats hit failed", ex);
        }

        try {
            boolean noFilters = text == null && paid == null && (categories == null || categories.isEmpty());
            boolean noDates = rangeStart == null && rangeEnd == null;

            LocalDateTime start = noFilters && noDates
                    ? LocalDateTime.now().minusYears(100)
                    : (rangeStart != null ? rangeStart : LocalDateTime.now());
            LocalDateTime end = (rangeEnd != null) ? rangeEnd : LocalDateTime.now().plusYears(100);

            Sort springSort = "EVENT_DATE".equalsIgnoreCase(sort) ? Sort.by("eventDate").ascending() : Sort.unsorted();
            Pageable pageable = PageUtils.offsetPage(from, size, springSort);

            List<Event> events;
            if (noFilters) {
                events = eventRepository.findByStateAndEventDateBetween(
                        EventState.PUBLISHED, start, end, pageable
                );
            } else {
                try {
                    if (categories == null || categories.isEmpty()) {
                        events = eventRepository.searchPublicNoCategories(text, paid, start, end, pageable);
                    } else {
                        events = eventRepository.searchPublicWithCategories(text, paid, categories, start, end, pageable);
                    }
                } catch (Exception ex) {
                    log.debug("Public search query failed", ex);
                    events = List.of();
                }
            }

            if (Boolean.TRUE.equals(onlyAvailable)) {
                Map<Long, Long> confirmed = confirmedByEvent(events);
                events = events.stream().filter(ev -> {
                    long conf = confirmed.getOrDefault(ev.getId(), 0L);
                    return ev.getParticipantLimit() == 0 || conf < ev.getParticipantLimit();
                }).toList();
            }

            List<EventShortDto> result = withShortViews(events);

            if ("VIEWS".equalsIgnoreCase(sort)) {
                result = result.stream()
                        .sorted(Comparator.comparingLong(EventShortDto::getViews).reversed())
                        .toList();
            }
            return result;
        } catch (Exception ex) {
            log.debug("Public events endpoint failed", ex);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public EventFullDto getPublicById(Long id, HttpServletRequest request) {
        try {
            stats.hit(request);
        } catch (Exception ex) {
            log.debug("Stats hit failed", ex);
        }
        Event e = getOr404(id);
        if (e.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие не опубликовано: " + id);
        }
        return withFullViews(e);
    }

    @Transactional(readOnly = true)
    public List<EventFullDto> searchAdmin(List<Long> users,
                                          List<EventState> states,
                                          List<Long> categories,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          int from,
                                          int size) {
        LocalDateTime start = rangeStart != null ? rangeStart : LocalDateTime.now().minusYears(100);
        LocalDateTime end = rangeEnd != null ? rangeEnd : LocalDateTime.now().plusYears(100);
        Pageable pageable = PageUtils.offsetPage(from, size, Sort.by("id").ascending());
        List<Event> events = eventRepository.searchAdmin(users, states, categories, start, end, pageable);
        return withFullViews(events);
    }

    public Event getOr404(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: " + id));
    }

    private void validateEventDateAtLeast2Hours(LocalDateTime dt, boolean creation) {
        if (dt == null) {
            throw new BadRequestException("Поле eventDate должно быть задано");
        }
        if (dt.isBefore(LocalDateTime.now().plusHours(2))) {
            String where = creation ? "создания" : "редактирования";
            throw new BadRequestException(
                    "Дата и время события не могут быть раньше, чем через 2 часа (проверка при " + where + ")"
            );
        }
    }

    private List<EventShortDto> withShortViews(List<Event> events) {
        Map<Long, Long> views = viewsByEvent(events);
        Map<Long, Long> conf = confirmedByEvent(events);
        return events.stream()
                .map(ev -> EventMapper.toShortDto(ev,
                        views.getOrDefault(ev.getId(), 0L),
                        conf.getOrDefault(ev.getId(), 0L)))
                .toList();
    }

    private EventFullDto withFullViews(Event e) {
        Map<Long, Long> views = viewsByEvent(List.of(e));
        Map<Long, Long> conf = confirmedByEvent(List.of(e));
        return EventMapper.toFullDto(e,
                views.getOrDefault(e.getId(), 0L),
                conf.getOrDefault(e.getId(), 0L));
    }

    private List<EventFullDto> withFullViews(List<Event> events) {
        Map<Long, Long> views = viewsByEvent(events);
        Map<Long, Long> conf = confirmedByEvent(events);
        return events.stream()
                .map(ev -> EventMapper.toFullDto(ev,
                        views.getOrDefault(ev.getId(), 0L),
                        conf.getOrDefault(ev.getId(), 0L)))
                .toList();
    }

    private Map<Long, Long> viewsByEvent(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }
        Set<Long> ids = events.stream().map(Event::getId).collect(Collectors.toSet());
        try {
            return stats.getViewsForEvents(ids, LocalDateTime.now().minusYears(5), LocalDateTime.now().plusYears(5));
        } catch (Exception ex) {
            log.debug("Stats views failed", ex);
            return Map.of();
        }
    }

    private Map<Long, Long> confirmedByEvent(List<Event> events) {
        Map<Long, Long> m = new HashMap<>();
        for (Event ev : events) {
            long c = requestRepository.countByEventIdAndStatus(ev.getId(), RequestStatus.CONFIRMED);
            m.put(ev.getId(), c);
        }
        return m;
    }
}
