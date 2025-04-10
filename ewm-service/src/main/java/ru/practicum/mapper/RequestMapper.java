package ru.practicum.mapper;

import ru.practicum.exception.ValidationException;
import ru.practicum.model.Request;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDtoFromRequest(Request request) {
        if (request == null) {
            throw new ValidationException("request can not be null");
        }
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setCreated(formatTimeWithNanoseconds(request.getCreated()));
        dto.setStatus(request.getStatus());
        dto.setEvent(request.getEvent().getId());
        return dto;
    }

    public static String formatTimeWithNanoseconds(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        return dateTime.format(formatter);
    }
}
