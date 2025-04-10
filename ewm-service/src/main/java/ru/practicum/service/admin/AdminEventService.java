package ru.practicum.service.admin;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventService {

    List<EventFullDto> getEventsFromAdmin(List<Long> users, List<String> states, List<Integer> categories,
                                          String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
