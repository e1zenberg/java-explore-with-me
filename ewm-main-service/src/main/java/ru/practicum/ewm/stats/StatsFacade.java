package ru.practicum.ewm.stats;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatsDto;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class StatsFacade {

    StatsClient statsClient;

    public void hit(HttpServletRequest req) {
        try {
            String xff = req.getHeader("X-Forwarded-For");
            String ip = xff != null && !xff.isBlank() ? xff.split(",")[0].trim() : req.getRemoteAddr();
            statsClient.hit("ewm-main-service", req.getRequestURI(), ip, LocalDateTime.now());
        } catch (Exception ex) {
            log.debug("Stats hit failed", ex);
        }
    }

    public Map<Long, Long> getViewsForEvents(Collection<Long> eventIds, LocalDateTime start, LocalDateTime end) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }
        try {
            List<String> uris = eventIds.stream().map(id -> "/events/" + id).toList();
            List<ViewStatsDto> views = statsClient.getStats(start, end, uris, true);
            Map<String, Long> byUri = new HashMap<>();
            for (ViewStatsDto v : views) {
                byUri.put(v.getUri(), v.getHits());
            }
            Map<Long, Long> result = new HashMap<>();
            for (Long id : eventIds) {
                result.put(id, byUri.getOrDefault("/events/" + id, 0L));
            }
            return result;
        } catch (Exception ex) {
            log.debug("Stats views failed", ex);
            return Map.of();
        }
    }
}
