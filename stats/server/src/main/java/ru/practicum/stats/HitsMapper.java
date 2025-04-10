package ru.practicum.stats;

import ru.practicum.dto.hit.HitRequest;

public class HitsMapper {
    public static HitEntity toEntity(HitRequest request) {
        if (request == null) {
            return null;
        }

        return HitEntity.builder()
                .app(request.getApp())
                .uri(request.getUri())
                .ip(request.getIp())
                .timestamp(request.getTimestamp())
                .build();
    }

}
