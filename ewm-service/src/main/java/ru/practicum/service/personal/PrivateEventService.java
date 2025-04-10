package ru.practicum.service.personal;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getEvents(Long userId,
                                  Integer from,
                                  Integer size);

    EventFullDto postEvent(Long userId,
                           NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto patchEvent(Long userId,
                            Long eventId,
                            UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequests(Long userId,
                                              Long eventId);

    EventRequestStatusUpdateResult patchRequests(Long userId,
                                                 Long eventId,
                                                 EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
