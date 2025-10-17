package ru.practicum.ewm.stats;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class StatsFacade {

    StatsClient statsClient;

    public void hit(HttpServletRequest req) {
        statsClient.hit("ewm-main-service", req.getRequestURI(), req.getRemoteAddr(), LocalDateTime.now());
    }

    public Map<Long, Long> getViewsForEvents(Collection<Long> eventIds, LocalDateTime start, LocalDateTime end) {
        if (eventIds == null || eventIds.isEmpty()) return Collections.emptyMap();
        List<String> uris = eventIds.stream().map(id -> "/events/" + id).toList();
        List<ViewStatsDto> views = statsClient.getStats(start, end, uris, true);
        Map<String, Long> byUri = views.stream().collect(Collectors.toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
        Map<Long, Long> result = new HashMap<>();
        for (Long id : eventIds) {
            result.put(id, byUri.getOrDefault("/events/" + id, 0L));
        }
        return result;
    }
}
