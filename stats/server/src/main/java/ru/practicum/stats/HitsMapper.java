package ru.practicum.stats;

import jakarta.validation.ValidationException;
import ru.practicum.dto.hit.HitRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HitsMapper {
    public static HitEntity toEntity(HitRequest request) {
        if (request == null) {
            return null;
        }

        return HitEntity.builder()
                .app(request.getApp())
                .uri(request.getUri())
                .ip(request.getIp())
                .timestamp(parseTimestamp(request.getTimestamp()))
                .build();
    }

    private static LocalDateTime parseTimestamp(String timestampStr) {
        try {
            int dotIndex = timestampStr.indexOf('.');
            if (dotIndex != -1) {
                timestampStr = timestampStr.substring(0, dotIndex);
            }
            return LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid timestamp format. Expected 'yyyy-MM-dd HH:mm:ss', got: " + timestampStr);
        }
    }
}
