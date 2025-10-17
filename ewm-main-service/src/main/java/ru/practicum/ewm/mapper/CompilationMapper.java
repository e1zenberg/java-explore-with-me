package ru.practicum.ewm.mapper;

import java.util.List;
import java.util.Map;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.model.Compilation;

public final class CompilationMapper {

    private CompilationMapper() {
    }

    public static CompilationDto toDto(Compilation c,
                                       Map<Long, Long> views,
                                       Map<Long, Long> confirmed) {
        List<EventShortDto> events = c.getEvents().stream()
                .map(e -> EventMapper.toShortDto(
                        e,
                        views.getOrDefault(e.getId(), 0L),
                        confirmed.getOrDefault(e.getId(), 0L)
                ))
                .toList();

        return CompilationDto.builder()
                .id(c.getId())
                .pinned(c.getPinned())
                .title(c.getTitle())
                .events(events)
                .build();
    }
}
