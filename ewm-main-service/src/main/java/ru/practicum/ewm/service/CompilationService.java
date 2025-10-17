package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.repo.CompilationRepository;
import ru.practicum.ewm.repo.ParticipationRequestRepository;
import ru.practicum.ewm.stats.StatsFacade;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationService {

    CompilationRepository compilationRepository;
    EventService eventService;
    ParticipationRequestRepository requestRepository;
    StatsFacade stats;

    public CompilationDto create(NewCompilationDto dto) {
        Compilation c = new Compilation();
        c.setPinned(Boolean.TRUE.equals(dto.getPinned()));
        c.setTitle(dto.getTitle());
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Event> events = dto.getEvents().stream()
                    .map(eventService::getOr404)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            c.setEvents(events);
        }
        Compilation saved = compilationRepository.save(c);
        return toDto(saved);
    }

    public void delete(Long id) {
        compilationRepository.delete(getOr404(id));
    }

    public CompilationDto update(Long id, UpdateCompilationRequest dto) {
        Compilation c = getOr404(id);
        if (dto.getPinned() != null) c.setPinned(dto.getPinned());
        if (dto.getTitle() != null) c.setTitle(dto.getTitle());
        if (dto.getEvents() != null) {
            Set<Event> events = dto.getEvents().stream()
                    .map(eventService::getOr404)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            c.setEvents(events);
        }
        return toDto(compilationRepository.save(c));
    }

    public List<CompilationDto> list(Boolean pinned, int from, int size) {
        List<Compilation> comps = compilationRepository.findAll().stream()
                .filter(c -> pinned == null || c.getPinned().equals(pinned))
                .toList();
        int start = Math.min(from, comps.size());
        int end = Math.min(from + size, comps.size());
        return comps.subList(start, end).stream().map(this::toDto).toList();
    }

    public CompilationDto getById(Long id) {
        return toDto(getOr404(id));
    }

    private Compilation getOr404(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена: " + id));
    }

    private CompilationDto toDto(Compilation c) {
        Set<Long> ids = c.getEvents().stream().map(Event::getId).collect(Collectors.toSet());
        Map<Long, Long> views = ids.isEmpty()
                ? Map.of()
                : stats.getViewsForEvents(ids, LocalDateTime.now().minusYears(5), LocalDateTime.now().plusYears(5));

        Map<Long, Long> confirmed = new HashMap<>();
        for (Long id : ids) {
            long cnt = requestRepository.countByEventIdAndStatus(id, RequestStatus.CONFIRMED);
            confirmed.put(id, cnt);
        }
        return CompilationMapper.toDto(c, views, confirmed);
    }
}
