package ru.practicum.mapper;

import ru.practicum.exception.ValidationException;
import ru.practicum.model.Request;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.time.LocalDateTime;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDtoFromRequest(Request request) {
        if (request == null) {
            throw new ValidationException("request can not be null");
        }
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setCreated(LocalDateTime.now());
        dto.setStatus(request.getStatus());
        dto.setEvent(request.getEvent().getId());
        return dto;
    }

}
