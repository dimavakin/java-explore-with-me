package ru.practicum.service.open;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.BadRequestException;
import ru.practicum.model.Event;
import ru.practicum.mapper.EventMapper;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public List<EventShortDto> getEvents(String text, List<Integer> categories, Boolean paid,
                                         String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                         String sort, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = parseDateTime(rangeStart).orElse(now);
        LocalDateTime end = parseDateTime(rangeEnd).orElse(null);
        sort = sort == null ? "EVENT_DATE" : sort.toUpperCase();

        if (end != null && end.isBefore(start)) {
            throw new BadRequestException("End date must be after start date");
        }

        Pageable pageable = createPageable(from, size, sort);

        List<Event> events = eventRepository.findPublicEvents(
                text != null ? text.trim().toLowerCase() : null,
                categories,
                paid,
                onlyAvailable,
                sort,
                pageable
        );

        List<Event> filteredEvents = events.stream()
                .filter(e -> (!e.getEventDate().isBefore(start)) &&
                        (end == null || !e.getEventDate().isAfter(end)))
                .toList();

        return filteredEvents.stream()
                .map(EventMapper::toEventShortDtoFromEvent).toList();
    }

    @Override
    public EventFullDto getEvent(Long id) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Published event with id=%d not found", id)));

        eventRepository.incrementViews(id);

        return EventMapper.toEventFullDtoFromEvent(event);
    }

    private Pageable createPageable(Integer from, Integer size, String sort) {
        if ("VIEWS".equalsIgnoreCase(sort)) {
            return PageRequest.of(from / size, size, Sort.by("views").descending());
        }
        return PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
    }


    private Optional<LocalDateTime> parseDateTime(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date format.");
        }
    }
}
