package ru.practicum.stats;

import jakarta.validation.ValidationException;
import ru.practicum.dto.hit.HitRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HitsMapper {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        if (timestampStr == null || timestampStr.isBlank()) {
            throw new ValidationException("Timestamp cannot be null or empty");
        }

        try {
            return LocalDateTime.parse(timestampStr, TIMESTAMP_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(
                    "Invalid timestamp format. Expected 'yyyy-MM-dd HH:mm:ss', got: " + timestampStr
            );
        }
    }
}
