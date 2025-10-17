package ru.practicum.stats.client;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

@RequiredArgsConstructor
public class StatsClient {

    private final String baseUrl;

    private final RestTemplate rest;

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

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
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/stats")
                .queryParam("start", start.format(FMT))
                .queryParam("end", end.format(FMT))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String u : uris) {
                builder.queryParam("uris", u);
            }
        }

        URI requestUri = builder.build().toUri();

        ResponseEntity<ViewStatsDto[]> resp = rest.getForEntity(requestUri, ViewStatsDto[].class);
        ViewStatsDto[] body = resp.getBody();
        return body == null ? Collections.emptyList() : Arrays.asList(body);
    }

}
