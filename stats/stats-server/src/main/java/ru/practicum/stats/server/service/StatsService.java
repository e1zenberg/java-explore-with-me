package ru.practicum.stats.server.service;

import java.util.List;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

public interface StatsService {

    void saveHit(EndpointHitDto dto);

    List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique);
}
