package ru.practicum.stats.client;

import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.hit.HitRequest;
import ru.practicum.dto.stats.StatsRequest;
import ru.practicum.dto.stats.StatsResponse;

import java.util.List;

public class StatsClient {
    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClient(RestTemplate restTemplate, String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    public void saveHit(HitRequest request) {
        restTemplate.postForEntity(serverUrl + "/hit", request, Void.class);
    }

    public List<StatsResponse> getStats(StatsRequest request) {
        return restTemplate.getForObject(
                serverUrl + "/stats?start={start}&end={end}&urls={urls}&unique={unique}",
                List.class,
                request.getStart(), request.getEnd(), request.getUris(), request.getUnique()
        );
    }
}
