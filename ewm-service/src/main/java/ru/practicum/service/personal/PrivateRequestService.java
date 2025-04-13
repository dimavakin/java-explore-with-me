package ru.practicum.service.personal;

import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateRequestService {
    List<ParticipationRequestDto> getRequestsFromUser(Long userId);

    ParticipationRequestDto postRequestFromUser(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestFromUser(Long userId, Long requestId);
}
