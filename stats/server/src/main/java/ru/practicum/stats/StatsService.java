package ru.practicum.stats;

import ru.practicum.dto.hit.HitRequest;
import ru.practicum.dto.stats.StatsResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    void saveHit(HitRequest request);

    List<StatsResponse> getStats(LocalDateTime start, LocalDateTime end,
                                        List<String> uris, boolean unique);
}