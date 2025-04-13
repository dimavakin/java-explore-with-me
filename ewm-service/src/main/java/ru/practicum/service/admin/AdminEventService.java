package ru.practicum.service.admin;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEventsFromAdmin(List<Long> users, List<String> states, List<Integer> categories,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
