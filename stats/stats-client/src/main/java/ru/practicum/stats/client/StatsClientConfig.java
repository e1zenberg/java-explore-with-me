package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StatsClientConfig {

    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    @Bean
    public RestTemplate statsRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public StatsClient statsClient(
            @Value("${stats.base-url:http://localhost:9090}") String baseUrl,
            RestTemplate statsRestTemplate
    ) {
        return new StatsClient(baseUrl, statsRestTemplate);
    }
}
