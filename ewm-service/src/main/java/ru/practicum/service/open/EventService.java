package ru.practicum.service.open;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

public interface EventService {
    List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid,
                                  String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                  String sort, Integer from, Integer size);

    EventFullDto getEvent(Long id, String ip);
}
