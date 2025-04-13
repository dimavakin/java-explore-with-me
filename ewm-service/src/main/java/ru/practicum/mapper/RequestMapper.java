package ru.practicum.mapper;

import ru.practicum.model.Request;
import ru.practicum.dto.request.ParticipationRequestDto;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDtoFromRequest(Request request) {
        if (request == null) {
            return null;
        }
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setCreated(request.getCreated());
        dto.setStatus(request.getStatus());
        dto.setEvent(request.getEvent().getId());
        return dto;
    }

}
