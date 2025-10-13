package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.DateFormats;
import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.server.repo.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository repository;

    @Override
    public void saveHit(EndpointHitDto dto) {
        // timestamp приходит строкой в требуемом формате
        LocalDateTime ts = LocalDateTime.parse(dto.getTimestamp(), DateFormats.DATE_TIME);
        Hit hit = Hit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(ts)
                .build();
        repository.save(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime from;
        LocalDateTime to;
        try {
            from = LocalDateTime.parse(start, DateFormats.DATE_TIME);
            to = LocalDateTime.parse(end, DateFormats.DATE_TIME);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Некорректный формат даты: ожидается yyyy-MM-dd HH:mm:ss");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("start должен быть меньше или равен end");
        }
        boolean urisEmpty = CollectionUtils.isEmpty(uris);
        return unique
                ? repository.aggregateUnique(from, to, uris, urisEmpty)
                : repository.aggregate(from, to, uris, urisEmpty);
    }
}
