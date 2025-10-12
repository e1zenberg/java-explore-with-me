package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
public class StatsClient {

    private final String baseUrl;      // например, http://stats-server:9090
    private final RestTemplate rest;   // передаётся извне

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void hit(String app, String uri, String ip, LocalDateTime timestamp) {
        EndpointHitDto dto = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp.format(FMT))
                .build();
        rest.postForLocation(baseUrl + "/hit", dto);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end,
                                       List<String> uris, boolean unique) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(baseUrl + "/stats")
                .queryParam("start", start.format(FMT))
                .queryParam("end", end.format(FMT))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String u : uris) {
                b.queryParam("uris", u);
            }
        }
        URI uri = b.build(true).toUri(); // true — не пере-энкодить уже корректные значения

        ResponseEntity<ViewStatsDto[]> resp = rest.getForEntity(uri, ViewStatsDto[].class);
        ViewStatsDto[] body = resp.getBody();
        return body == null ? Collections.emptyList() : Arrays.asList(body);
    }
}
